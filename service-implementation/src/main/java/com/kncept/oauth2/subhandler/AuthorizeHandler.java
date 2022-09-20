package com.kncept.oauth2.subhandler;

import com.kncept.oauth2.config.Oauth2Configuration;
import com.kncept.oauth2.config.authcode.Authcode;
import com.kncept.oauth2.config.authrequest.AuthRequest;
import com.kncept.oauth2.config.client.Client;
import com.kncept.oauth2.config.session.OauthSession;
import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RedirectResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.kncept.oauth2.util.ParamUtils.optional;
import static com.kncept.oauth2.util.ParamUtils.required;

public class AuthorizeHandler {

    private final Oauth2Configuration config;

    enum ResponseType {
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

    public AuthorizeHandler(Oauth2Configuration config) {
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

        boolean isPkce = config.requirePkce() || params.containsKey("code_challenge");
        if (isPkce) {
            String codeChallenge = required("code_challenge", params);
            String codeChallengeMethod = optional("code_challenge_method", params, "S256");
            String code_verifier = optional("code_verifier", params, null);
        }

        Optional<Client> client = config.clientRepository().lookup(clientId);
        if (client.isEmpty()) {
            return new ContentResponse(
                    400,
                    ContentResponse.Content.ERROR_PAGE,
                    oauthSessionId)
                    .withParam("error", "Unknown Client ID: " + clientId);
        }

        // TODO: Dynamic Code Generator

        // join an existing auth session if possible
        if (oauthSessionId.isPresent()) {
            Optional<OauthSession> session = config.oauthSessionRepository().lookup(oauthSessionId.get());
            if (session.isPresent() && session.get().authenticated()) {
                // for each new inbound auth request, just create a new auth request.
                // otherwise old state can be picked up
                AuthRequest ar = config.authRequestRepository().createAuthRequest(
                        oauthSessionId.get(),
                        state,
                        nonce,
                        redirectUri,
                        clientId,
                        responseType
                );
                return redirectAfterSuccessfulAuth(oauthSessionId.get(), ar);
            }
        }

        // create new session then
        OauthSession session = config.oauthSessionRepository().createSession();
        config.authRequestRepository().createAuthRequest(
                session.oauthSessionId(),
                state,
                nonce,
                redirectUri,
                clientId,
                responseType
        );

        return new ContentResponse(
                200,
                ContentResponse.Content.LOGIN_PAGE,
                Optional.of(session.oauthSessionId()))
                .withParam("acceptingSignup", Boolean.toString(config.userRepository().acceptingSignup()));
    }

    private OperationResponse redirectAfterSuccessfulAuth(String oauthSessionId, AuthRequest authRequest) {
        try {
            // redirect back to app.
            //
            // Potential option - use an interposing screen.
            // Use case - ignoring redirect URI and using this service
            // as an 'index' service
            // eg: these services have been authorized
            //   - app1
            //   - app2

            String redirectUri = authRequest.redirectUri();

            if (!redirectUri.endsWith("?")) {
                redirectUri = redirectUri + "?";
            }

            Authcode authCode = config.authcodeRepository().create(UUID.randomUUID().toString(), oauthSessionId);
            redirectUri = redirectUri + "code=" + URLEncoder.encode(authCode.authCode(), "UTF8");

            Optional<String> state = authRequest.state();
            if (state.isPresent()) redirectUri = redirectUri + "&state=" + URLEncoder.encode(state.get(), "UTF8");

            return new RedirectResponse(redirectUri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
