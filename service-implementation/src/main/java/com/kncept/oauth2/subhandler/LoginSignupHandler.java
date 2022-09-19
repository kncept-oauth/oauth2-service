package com.kncept.oauth2.subhandler;

import com.kncept.oauth2.config.Oauth2Configuration;
import com.kncept.oauth2.config.authcode.Authcode;
import com.kncept.oauth2.config.authrequest.AuthRequest;
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

public class LoginSignupHandler {

    private final Oauth2Configuration config;

    public LoginSignupHandler(Oauth2Configuration config) {
        this.config = config;
    }

    public OperationResponse login(Map<String, String> params, String oauthSessionId) {
        if (oauthSessionId == null) throw new NullPointerException("Must have a session ID");
        Optional<OauthSession> session = null; //config.oauthSessionRepository().lookup(oauthSessionId);

        if (params.isEmpty() || !params.containsKey("password")) // just display login page with no attempts at anything else
            return new ContentResponse(
                    200,
                    ContentResponse.Content.LOGIN_PAGE,
                    Optional.of(oauthSessionId))
                    .withParam("acceptingSignup", Boolean.toString(config.userRepository().acceptingSignup()));


        // its a login attempt
        String password = params.get("password");
        String username = params.get("username");

        Optional<User> endUser = config.userRepository().login(username, password);

//        https://openid.net/specs/openid-connect-core-1_0.html#AuthResponse
        if (endUser.isPresent()) {
            session = config.oauthSessionRepository().authenticate(oauthSessionId, endUser.get().userId());

            Optional<AuthRequest> authRequest = config.authRequestRepository().lookup(oauthSessionId);
            if (authRequest.isEmpty()) {
                return new ContentResponse(
                        400,
                        ContentResponse.Content.ERROR_PAGE,
                        Optional.of(oauthSessionId))
                        .withParam("error", "OIDC Auth Request Timed out");
            }
            return redirectAfterSuccessfulAuth(oauthSessionId, authRequest.get());
        } else {
            return new ContentResponse(
                    200,
                    ContentResponse.Content.LOGIN_PAGE,
                    Optional.of(oauthSessionId))
                    .withParam("acceptingSignup", Boolean.toString(config.userRepository().acceptingSignup()))
                    .withParam("message", "Authorization Failed - Please try again");
        }
    }

    public OperationResponse signup(Map<String, String> params, String oauthSessionId) {
        if (!config.userRepository().acceptingSignup())
            return new ContentResponse(
                    400,
                    ContentResponse.Content.ERROR_PAGE,
                    Optional.of(oauthSessionId))
                    .withParam("error", "Signup is not currently enabled");

        if (params.isEmpty() || !params.containsKey("password")) // just display signup page with no attempts at anything else
            return new ContentResponse(
                    200,
                    ContentResponse.Content.SIGNUP_PAGE,
                    Optional.of(oauthSessionId));

        // attempt signup
        String password = params.get("password");
        String username = params.get("username");
        Optional<User> endUser = config.userRepository().create(username, password);

        Optional<AuthRequest> authRequest = config.authRequestRepository().lookup(oauthSessionId);
        if (authRequest.isEmpty()) {
            return new ContentResponse(
                    400,
                    ContentResponse.Content.ERROR_PAGE,
                    Optional.of(oauthSessionId))
                    .withParam("error", "OIDC Auth Request Timed out");
        }

        if (endUser.isPresent()) {
            config.oauthSessionRepository().authenticate(oauthSessionId, endUser.get().userId());
            return redirectAfterSuccessfulAuth(oauthSessionId, authRequest.get());
        }

        return new ContentResponse(
                200,
                ContentResponse.Content.SIGNUP_PAGE,
                Optional.of(oauthSessionId))
                .withParam("message", "Signup failed");
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
