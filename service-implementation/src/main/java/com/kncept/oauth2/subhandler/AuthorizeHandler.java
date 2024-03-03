package com.kncept.oauth2.subhandler;

import com.kncept.oauth2.config.Oauth2StorageConfiguration;
import com.kncept.oauth2.config.authrequest.AuthRequest;
import com.kncept.oauth2.config.client.Client;
import com.kncept.oauth2.config.parameter.ConfigParameters;
import com.kncept.oauth2.config.session.OauthSession;
import com.kncept.oauth2.config.user.User;
import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RedirectResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.kncept.oauth2.util.DateUtils.utcNow;
import static com.kncept.oauth2.util.ParamUtils.optional;
import static com.kncept.oauth2.util.ParamUtils.required;

public class AuthorizeHandler {

    long sessionDuration = TimeUnit.SECONDS.toSeconds(300);

    private final Oauth2StorageConfiguration config;

    public enum ResponseType {
        code ; //, token;
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

    public AuthorizeHandler(Oauth2StorageConfiguration config) {
        this.config = config;
    }


    // https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
    public OperationResponse authorize(Map<String, String> params, Optional<String> oauthSessionId) {
        String scope = required("scope", params); // meant to have 'openid' in it for OIDC
        String responseType = required("response_type", params); // eg: code
        ResponseType.validate(responseType); // inline validation :/ TODO: fix this
        String clientId = required("client_id", params);
        String redirectUri = required("redirect_uri", params);

        Optional<String> state = optional("state", params);
        Optional<String> nonce = optional("nonce", params);

        Client client = config.clientRepository().read(Client.id(clientId));
        if (client == null) {
            return new ContentResponse(
                    400,
                    ContentResponse.Content.ERROR_PAGE,
                    oauthSessionId)
                    .withParam("error", "Unknown Client ID: " + clientId);
        }
        // if Client PKCE required and no PKCE then error "PKCE Code Challenge Required"


        boolean pkce = client.isRequirePkce() || params.containsKey("code_challenge");
        String pkceCodeChallenge = null;
        String pkceChallengeType = null;
        if (pkce) {
            pkceCodeChallenge = required("code_challenge", params);
            pkceChallengeType = optional("code_challenge_method", params, "plain");
            if (
                    !"plain".equalsIgnoreCase(pkceChallengeType) &&
                    !"S256".equalsIgnoreCase(pkceChallengeType)
            ) return new ContentResponse(
                    400,
                    ContentResponse.Content.ERROR_PAGE,
                    oauthSessionId)
                    .withParam("error", "PKCE Challenge type is not valid");
        }

        boolean redirectUrlIsValid = false;
        if (client.getEndpoints() != null)
        for(String endpoint: client.getEndpoints()) {
            if (endpoint.endsWith("*")) {
                redirectUrlIsValid |= redirectUri.startsWith(endpoint.substring(0, endpoint.length() - 1));
            } else {
                redirectUrlIsValid |= redirectUri.equals(endpoint);
            }
        }
        if (!redirectUrlIsValid) return new ContentResponse(
                400,
                ContentResponse.Content.ERROR_PAGE,
                oauthSessionId)
                .withParam("error", "Redirect URL is not valid: " + redirectUri);

        // TODO: Dynamic Code Generator

        // join an existing auth session if possible
        if (oauthSessionId.isPresent()) {
            OauthSession session = config.oauthSessionRepository().read(OauthSession.id(oauthSessionId.get()));
            if (session != null && session.getRef().type.equals(User.EntityType) && session.getExpiry().isAfter(utcNow())) {
                // for each new inbound auth request, just create a new auth request.
                // otherwise old state can be picked up
                AuthRequest ar = config.authRequestRepository().read(AuthRequest.id(oauthSessionId.get()));
                if (ar != null) config.authRequestRepository().delete(ar);
//                if (ar == null) {
                    ar = new AuthRequest();
                    ar.setId(AuthRequest.id(oauthSessionId.get()));
                    ar.setState(state);
                    ar.setNonce(nonce);
                    ar.setRedirectUri(redirectUri);
                    ar.setRef(client.getId());
                    ar.setResponseType(responseType);
                    ar.setExpiry(utcNow().plusMinutes(5));

                    ar.setUserId(session.getRef());
                    ar.setOauthSessionId(OauthSession.id(oauthSessionId.get()));

                    ar.setPkce(pkce);
                    ar.setPkceCodeChallenge(pkceCodeChallenge);
                    ar.setPkceChallengeType(pkceChallengeType);

//                } else {
//                    if (ar.getExpiry().isBefore(utcNow())) {
//                        // expired. bad
//                        return new ContentResponse(
//                             400,
//                                ContentResponse.Content.ERROR_PAGE,
//                                Optional.of("")
//                        ).withParam("error", "Session Expired");
//                    }
//                }

                config.authRequestRepository().create(ar);
                return redirectAfterSuccessfulAuth(ar);
            }
        }

        // create new session then
        OauthSession session = new OauthSession();
        session.setId(OauthSession.id(UUID.randomUUID().toString()));
        session.setRef(session.getId()); // TODO: This is a bit vexing
        session.setExpiry(utcNow().plusSeconds(sessionDuration));
        config.oauthSessionRepository().create(session);

        AuthRequest ar = new AuthRequest();
        ar.setId(AuthRequest.id(session.getId().value));
        ar.setState(state);
        ar.setNonce(nonce);
        ar.setRedirectUri(redirectUri);
        ar.setRef(client.getId());
        ar.setResponseType(responseType);
        ar.setExpiry(utcNow().plusMinutes(5));

//        ar.setUserId(); // no user yet (!!)
        ar.setOauthSessionId(session.getId());

        ar.setPkce(pkce);
        ar.setPkceCodeChallenge(pkceCodeChallenge);
        ar.setPkceChallengeType(pkceChallengeType);

        config.authRequestRepository().create(ar);

        return new ContentResponse(
                200,
                ContentResponse.Content.LOGIN_PAGE,
                Optional.of(session.getId().value))
                .withParam("acceptingSignup", Boolean.toString(acceptingSignup()));
    }

    boolean acceptingSignup() {
        return Boolean.valueOf(ConfigParameters.signupEnabled.get(config.parameterRepository()));
    }

    public static OperationResponse redirectAfterSuccessfulAuth(AuthRequest authRequest) {
        if (authRequest.getUserId() == null) throw new IllegalStateException("User much be authorized to redirectAfterSuccessfulAuth");
        try {
            String redirectUri = authRequest.getRedirectUri();

            if (!redirectUri.contains("?")) { //no query part at all (add it)
                redirectUri = redirectUri + "?";
            } else if (!redirectUri.endsWith("?")) { // not at the very start of a query part, so append a param
                redirectUri = redirectUri + "&";
            }

            redirectUri = redirectUri + "code=" + URLEncoder.encode(authRequest.getId().value, "UTF8");

            Optional<String> state = authRequest.getState();
            if (state.isPresent()) redirectUri = redirectUri + "&state=" + URLEncoder.encode(state.get(), "UTF8");

            return new RedirectResponse(redirectUri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
