package com.kncept.oauth2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kncept.oauth2.authrequest.AuthRequest;
import com.kncept.oauth2.authrequest.SimpleAuthRequest;
import com.kncept.oauth2.client.Client;
import com.kncept.oauth2.configuration.Oauth2Configuration;
import com.kncept.oauth2.crypto.ExpiringKeyPair;
import com.kncept.oauth2.crypto.KeyVendor;
import com.kncept.oauth2.operation.response.OperationResponse;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
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

    public Oauth2() {
    }

    // https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
    public OperationResponse authorize(Map<String, String> params) {
        String scope = required("scope", params); // meant to have 'openid' in it for OIDC
        String responseType = required("response_type", params); // eg: code
        ResponseType.validate(responseType); // inline validation :/ TODO: fix this
        String clientId = required("client_id", params);
        String redirectUri = required("redirect_uri", params);

        String state = optional("state", params, null);
//        String nonce = optional("nonce", params, null);

        boolean isPkce = config.requirePkce() || params.containsKey("code_challenge");
        if (isPkce) {
            String codeChallenge = required("code_challenge", params);
            String codeChallengeMethod = optional("code_challenge_method", params, "S256");
            String code_verifier = optional("code_verifier", params, null);
        }

        Client client = config.clientRepository().getClientById(clientId);
        if (client == null) {
            return new OperationResponse(
                    OperationResponse.ResponseType.ERROR_HTML,
                    config.htmlPageVendor().errorPage(
                            "Unknown Client ID: " + clientId
                    ));
        }

        String callbackCode = UUID.randomUUID().toString();

        String oauthSessionId = config.authRequestRepository().createAuthRequest(
                new SimpleAuthRequest(state, redirectUri, clientId, responseType, callbackCode));

        return new OperationResponse(
                OperationResponse.ResponseType.OK_HTML,
                config.htmlPageVendor().loginPage(
                        oauthSessionId,
                        null,
                        config.userRepository().isAcceptingSignup()
                ));
    }

    public OperationResponse login(Map<String, String> params) throws IOException {
        String oauthSessionId = params.get("oauthSessionId");
        if (oauthSessionId == null) {
            return new OperationResponse(
                    OperationResponse.ResponseType.ERROR_HTML,
                    config.htmlPageVendor().errorPage(
                            "No oauthSessionId"
            ));
        }
        if (params.isEmpty() || !params.containsKey("password")) // just display login page with no attempts at anything else
            return new OperationResponse(
                    OperationResponse.ResponseType.OK_HTML,
                    config.htmlPageVendor().loginPage(
                            oauthSessionId,
                            null,
                            config.userRepository().isAcceptingSignup()
                    ));


        // its a login attempt
        String password = params.get("password");
        String username = params.get("username");

        boolean isAuthorized = config.userRepository().verifyUser(password, username);

//        https://openid.net/specs/openid-connect-core-1_0.html#AuthResponse
        if (isAuthorized) {
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

            String redirectUri = authRequest.getRedirectUri();

            if (!redirectUri.endsWith("?")) {
                redirectUri = redirectUri + "?";
            }

            redirectUri = redirectUri + "code=" + URLEncoder.encode(authRequest.getCode(), "UTF8");

            String state = authRequest.getState();
            if (state != null) redirectUri = redirectUri + "&state=" + URLEncoder.encode(state, "UTF8");

            return new OperationResponse(
                    OperationResponse.ResponseType.REDIRECT,
                    redirectUri);

        } else {
            return new OperationResponse(
                    OperationResponse.ResponseType.OK_HTML,
                    config.htmlPageVendor().loginPage(
                            oauthSessionId,
                            "Authorization Failed - Please try again",
                            config.userRepository().isAcceptingSignup()
                    ));
        }
    }

    public OperationResponse signup(Map<String, String> params) {
        if (!config.userRepository().isAcceptingSignup())
            return new OperationResponse(
                    OperationResponse.ResponseType.ERROR_HTML,
                    config.htmlPageVendor().errorPage(
                    "Signup is not currently enabled"
            ));
        return new OperationResponse(
                OperationResponse.ResponseType.ERROR_HTML,
                config.htmlPageVendor().errorPage(
                "Signup is not currently implemented"
        ));
    }

    // https://openid.net/specs/openid-connect-core-1_0.html#TokenEndpoint
    public OperationResponse token(Map<String, String> params) {

        String grantType = required("grant_type", params);
        // authorization_code

        // requires 'grantType' was authorization_code ?
        String code = required("code", params);
        //code_verifier ? // https://developer.okta.com/docs/reference/api/oidc/#request-parameters-4

        AuthRequest authRequest = config.authRequestRepository().lookupByCode(code);

        if (authRequest == null) {
            JSONObject obj = new JSONObject();
            obj.put("error", "No matching auth codes");
            return new OperationResponse(
                    OperationResponse.ResponseType.CLIENT_ERROR_JSON,
                    obj.toJSONString());
        }
        String responseType = authRequest.getResponseType(); // code vs authorization code

        // redirect_uri for 'authorization code' - same redirect URI as for the original auth request??

        // if the code matches, VEND a JWT token!!
        // https://github.com/auth0/java-jwt
//        HS256 = Algorithm.HMAC256
//        RS256 = Algorithm.RSA256

        ExpiringKeyPair keys = keyVendor.getPair();
        Algorithm algorithm = Algorithm.RSA256(
                (RSAPublicKey)keys.keyPair().getPublic(),
                (RSAPrivateKey)keys.keyPair().getPrivate());
        String token = JWT.create()
                .withIssuer("kncept-oauth")
                .withIssuedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                .sign(algorithm);

        JSONObject jwt = new JSONObject();

        jwt.put("token_type", "Bearer");
        jwt.put("id_token", token);
        jwt.put("expires_in", 3600);

//        jwt.put("refresh_token", "xxxx")

        // needs to be json
        return new OperationResponse(
                OperationResponse.ResponseType.OK_JSON,
                jwt.toJSONString());
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



}
