package com.kncept.oauth2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kncept.oauth2.authrequest.AuthRequest;
import com.kncept.oauth2.client.Client;
import com.kncept.oauth2.configuration.Oauth2Configuration;
import com.kncept.oauth2.crypto.ExpiringKeyPair;
import com.kncept.oauth2.crypto.KeyVendor;
import com.kncept.oauth2.operation.response.*;
import com.kncept.oauth2.session.OauthSession;
import com.kncept.oauth2.user.User;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Oauth2 {

    enum ResponseType {
            code, token;
            public static ResponseType fromString(String name) {
                for(ResponseType type: values())
                    if (type.name().equals(name))
                        return type;
                return null;
            }
            public static void validate(String name) {
                if (fromString(name) == null)
                    throw new RuntimeException("Unknown Response Type: " + name);
            }
    }

    private Oauth2Configuration config = new Oauth2Configuration();
    private KeyVendor keyVendor = new KeyVendor();

    public Oauth2(Oauth2Configuration config) {
        if (config != null) this.config = config;
    }
    public Oauth2() {
    }

    // https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
    public OperationResponse authorize(Map<String, String> params, Optional<String> oauthSessionId) throws IOException {
        String scope = required("scope", params); // meant to have 'openid' in it for OIDC
        String responseType = required("response_type", params); // eg: code
        ResponseType.validate(responseType); // inline validation :/ TODO: fix this
        String clientId = required("client_id", params);
        String redirectUri = required("redirect_uri", params);

        Optional<String> state = optional("state", params);
        Optional<String> nonce = optional("nonce", params);

        boolean isPkce = config.requirePkce() || params.containsKey("code_challenge");
        if (isPkce) {
            String codeChallenge = required("code_challenge", params);
            String codeChallengeMethod = optional("code_challenge_method", params, "S256");
            String code_verifier = optional("code_verifier", params, null);
        }

        Client client = config.clientRepository().getClientById(clientId);
        if (client == null) {
            return new ContentResponse(
                    400,
                    ContentResponse.ContentType.ERROR_PAGE,
                    oauthSessionId)
                    .withParam("error", "Unknown Client ID: " + clientId);
        }

        // TODO: Dynamic Code Generator
        String code = UUID.randomUUID().toString();

        // join an existing auth session if possible //a4fe4ef5-3aa7-4917-a446-f541d82c6399
        if (oauthSessionId.isPresent() && config.oauthSessionRepository().lookupSession(oauthSessionId.get()).isPresent()) {
            AuthRequest ar = config.authRequestRepository().lookupByOauthSessionId(oauthSessionId.get());
            if (ar == null) config.authRequestRepository().createAuthRequest(
                    oauthSessionId.get(),
                    code,
                    state,
                    nonce,
                    redirectUri,
                    clientId,
                    responseType
            );
            return redirectAfterSuccessfulAuth(oauthSessionId.get(), null);
        }

        // create new session then
        OauthSession session = config.oauthSessionRepository().createSession();
        config.authRequestRepository().createAuthRequest(
                session.oauthSessionId(),
                code,
                state,
                nonce,
                redirectUri,
                clientId,
                responseType
        );

        return new ContentResponse(
                200,
                ContentResponse.ContentType.LOGIN_PAGE,
                Optional.of(session.oauthSessionId()))
                .withParam("acceptingSignup", Boolean.toString(config.userRepository().isAcceptingSignup()));
    }

    public OperationResponse login(Map<String, String> params, String oauthSessionId) throws IOException {
        if (oauthSessionId == null) throw new NullPointerException("Must have a session ID");
        if (params.isEmpty() || !params.containsKey("password")) // just display login page with no attempts at anything else
            return new ContentResponse(
                    200,
                    ContentResponse.ContentType.LOGIN_PAGE,
                    Optional.of(oauthSessionId))
                    .withParam("acceptingSignup", Boolean.toString(config.userRepository().isAcceptingSignup()));


        // its a login attempt
        String password = params.get("password");
        String username = params.get("username");

        User endUser = config.userRepository().lookupUser(username, password);

//        https://openid.net/specs/openid-connect-core-1_0.html#AuthResponse
        if (endUser != null) {
            return redirectAfterSuccessfulAuth(oauthSessionId, endUser);
        } else {
            return new ContentResponse(
                    200,
                    ContentResponse.ContentType.LOGIN_PAGE,
                    Optional.of(oauthSessionId))
                    .withParam("acceptingSignup", Boolean.toString(config.userRepository().isAcceptingSignup()))
                    .withParam("message", "Authorization Failed - Please try again");
        }
    }

    public OperationResponse signup(Map<String, String> params, String oauthSessionId) throws IOException {
        if (!config.userRepository().isAcceptingSignup())
            return new ContentResponse(
                    400,
                    ContentResponse.ContentType.ERROR_PAGE,
                    Optional.of(oauthSessionId))
                    .withParam("error", "Signup is not currently enabled");

        if (params.isEmpty() || !params.containsKey("password")) // just display signup page with no attempts at anything else
            return new ContentResponse(
                    200,
                    ContentResponse.ContentType.SIGNUP_PAGE,
                    Optional.of(oauthSessionId));

        // attempt signup
        String password = params.get("password");
        String username = params.get("username");
        User endUser = config.userRepository().createUser(username, password);
        if (endUser != null) return redirectAfterSuccessfulAuth(oauthSessionId, endUser);

        return new ContentResponse(
                200,
                ContentResponse.ContentType.SIGNUP_PAGE,
                Optional.of(oauthSessionId))
                .withParam("message", "Signup failed");
    }

    private OperationResponse redirectAfterSuccessfulAuth(String oauthSessionId, User endUser) throws IOException {
        // redirect back to app.
        //
        // Potential option - use an interposing screen.
        // Use case - ignoring redirect URI and using this service
        // as an 'index' service
        // eg: these services have been authorized
        //   - app1
        //   - app2
        AuthRequest authRequest = config.authRequestRepository().lookupByOauthSessionId(oauthSessionId);
        // handle expired?

        String redirectUri = authRequest.redirectUri();

        if (!redirectUri.endsWith("?")) {
            redirectUri = redirectUri + "?";
        }

        redirectUri = redirectUri + "code=" + URLEncoder.encode(authRequest.code(), "UTF8");
        // ADD a CODE store
//            code -> username (and sub)

        Optional<String> state = authRequest.state();
        if (state.isPresent()) redirectUri = redirectUri + "&state=" + URLEncoder.encode(state.get(), "UTF8");

        return new RedirectResponse(redirectUri);
    }

    // https://openid.net/specs/openid-connect-core-1_0.html#TokenEndpoint
    public RenderedContentResponse token(Map<String, String> params) {
        Optional<String> oauthSessionId = Optional.empty();
        try {
            String grantType = required("grant_type", params);
            // authorization_code

            // requires 'grantType' was authorization_code ?
            String code = required("code", params);
            //code_verifier ? // https://developer.okta.com/docs/reference/api/oidc/#request-parameters-4

            AuthRequest authRequest = config.authRequestRepository().lookupByCode(code);

            if (authRequest == null) {
                JSONObject obj = new JSONObject();
                obj.put("error", "No matching auth codes");
                return new RenderedContentResponse(400, obj.toJSONString(), oauthSessionId);
            }
            String responseType = authRequest.responseType(); // code vs authorization code
            // redirect_uri for 'authorization code' - same redirect URI as for the original auth request??

            // if the code matches, VEND a JWT token!!
            // https://github.com/auth0/java-jwt

            ExpiringKeyPair keys = keyVendor.getPair();
            Algorithm algorithm = Algorithm.RSA256(
                    (RSAPublicKey) keys.keyPair().getPublic(),
                    (RSAPrivateKey) keys.keyPair().getPrivate());
            String token = JWT.create()
                    .withIssuer("kncept-oauth")
                    .withSubject(code) // TODO: This should be a 'user id', from the (todo) code lookup
                    .withIssuedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                    .withExpiresAt(LocalDateTime.now().plusHours(18).toInstant(ZoneOffset.UTC))
//                    .withClaim("nonce", authRequest.getnonce)
                    .sign(algorithm);

            JSONObject jwt = new JSONObject();
            jwt.put("token_type", "Bearer");
            jwt.put("id_token", token);
            jwt.put("expires_in", 3600);
            //        jwt.put("refresh_token", "xxxx")

            // needs to be json
            return new RenderedContentResponse(200, jwt.toJSONString(), oauthSessionId)
                    .addHeader("Cache-Control", "no-store")
                    .addHeader("Pragma", "no-cache");

        } catch (RuntimeException e) {
            JSONObject obj = new JSONObject();
            obj.put("error", e.getMessage());
            return new RenderedContentResponse(400, obj.toJSONString(), oauthSessionId);
        }
    }


    public RenderedContentResponse render(ContentResponse response) {
        return new RenderedContentResponse(response.responseCode(),content(response), response.oauthSessionId());
    }

    public String content(ContentResponse response) {
        Map<String, String> params = response.params();
        switch(response.type()) {
            case ERROR_PAGE -> {
                return config.htmlPageVendor().errorPage(
                        required("error", params)
                );
            }
            case LOGIN_PAGE -> {
                return config.htmlPageVendor().loginPage(
                        optional("message", params),
                        Boolean.parseBoolean(optional("acceptingSignup", params).orElse(null))
                );
            }
            case SIGNUP_PAGE -> {
                return config.htmlPageVendor().signupPage(optional("message", params));
            }
            case CSS -> {
                return config.htmlPageVendor().css();
            }
        }
        throw new IllegalStateException("Unkonwn response type: " + response.type());
    }



    private String required(String name, Map<String, String> params) {
        String value = params.get(name);
        if (value == null) throw new RuntimeException("Required Param missing: " + name);
        value = value.trim();
        if (value.equals("")) throw new RuntimeException("Required Param is empty: " + name);
        return value;
    }

    private String optional(String name, Map<String, String> params, String defaultValue) {
        String value = params.get(name);
        if (value == null) return defaultValue;
        value = value.trim();
        if (value.equals("")) return defaultValue;
        return value;
    }

    // trims null and empty to empty
    private Optional<String> optional(String name, Map<String, String> params) {
        String value = params.get(name);
        if (value == null) return Optional.empty();
        value = value.trim();
        if (value.equals("")) return Optional.empty();
        return Optional.of(value);
    }




}
