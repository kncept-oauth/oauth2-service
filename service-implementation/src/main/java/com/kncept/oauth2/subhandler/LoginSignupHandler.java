package com.kncept.oauth2.subhandler;

import com.kncept.oauth2.config.Oauth2StorageConfiguration;
import com.kncept.oauth2.config.authcode.Authcode;
import com.kncept.oauth2.config.authrequest.AuthRequest;
import com.kncept.oauth2.config.parameter.ConfigParameters;
import com.kncept.oauth2.config.parameter.Parameter;
import com.kncept.oauth2.config.session.OauthSession;
import com.kncept.oauth2.config.user.User;
import com.kncept.oauth2.config.user.UserLogin;
import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RedirectResponse;
import com.kncept.oauth2.util.PasswordUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.kncept.oauth2.util.DateUtils.utcNow;

public class LoginSignupHandler {

    private final Oauth2StorageConfiguration config;

    public LoginSignupHandler(Oauth2StorageConfiguration config) {
        this.config = config;
    }

    public OperationResponse signup(Map<String, String> params, String oauthSessionId) {
//        if (oauthSessionId != null && !oauthSessionId.isEmpty())
//            return new ContentResponse(
//                    400,
//                    ContentResponse.Content.ERROR_PAGE,
//                    Optional.ofNullable(oauthSessionId))
//                    .withParam("error", "Already signed in.");
        if (!acceptingSignup())
            return new ContentResponse(
                    400,
                    ContentResponse.Content.ERROR_PAGE,
                    Optional.ofNullable(oauthSessionId))
                    .withParam("error", "Signup is not currently enabled");

        String password = params.get("password");
        password = password == null ? "" : password.trim();
        if (password.isBlank()) // just display signup page with no attempts at anything else
            return new ContentResponse(
                    200,
                    ContentResponse.Content.SIGNUP_PAGE,
                    Optional.ofNullable(oauthSessionId))
                    .withParam("message", "Please enter a password");

        // attempt signup
        UserLogin.UserLoginType loginType = supportedLoginType(params.get("signtype"));
        if (loginType == null) return new ContentResponse(
                400,
                ContentResponse.Content.SIGNUP_PAGE,
                Optional.ofNullable(oauthSessionId))
                .withParam("message", "Choose a signup type");

        String externalcontact = params.get("externalcontact");
        if (externalcontact != null) externalcontact = loginType.normalize(externalcontact);
        if (!loginType.isValid(externalcontact)) return new ContentResponse(
                400,
                ContentResponse.Content.SIGNUP_PAGE,
                Optional.ofNullable(oauthSessionId))
                .withParam("message", "Invalid " + loginType.name());
        UserLogin existing = config.userLoginRepository().read(EntityId.parse(loginType.toString(), externalcontact));
        if (existing != null) return new ContentResponse(
                400,
                ContentResponse.Content.ERROR_PAGE,
                Optional.ofNullable(oauthSessionId))
                .withParam("error", loginType.name() + " already signed up");
        LocalDateTime now = utcNow();
        User user = new User();
        user.setId(User.id());
        user.setUsername(externalcontact);
        user.setSalt(PasswordUtils.generateSalt());
        user.setPassword(PasswordUtils.hash(user.getSalt(), password));
        user.setWhen(now);
        config.userRepository().create(user);

        UserLogin login = new UserLogin();
        login.setId(UserLogin.id(loginType, externalcontact));
        login.setRef(user.getId());


        // TODO: This _should_ be configurable
//        Duration duration = Duration.parse("PT10m");
        login.setTokenValue(PasswordUtils.randomString(8, PasswordUtils.alphanumeric));
        login.setTokenIssued(now);
//        login.setTokenExpires(now.plusSeconds(duration.toSeconds()));
        login.setTokenExpires(now.plusMinutes(10));

        config.userLoginRepository().create(login);
        return new ContentResponse(
                400,
                ContentResponse.Content.VERIFY_PAGE,
                Optional.ofNullable(oauthSessionId))
                .withParam("message", "Successfully signed up. Please verify your " + loginType + " " + externalcontact)
                .withParam("externalcontact", externalcontact);

    }

    public OperationResponse verify(Map<String, String> params, String oauthSessionId) {
//        if (oauthSessionId != null && !oauthSessionId.isEmpty())
//            return new ContentResponse(
//                    400,
//                    ContentResponse.Content.ERROR_PAGE,
//                    Optional.ofNullable(oauthSessionId))
//                    .withParam("error", "Already signed in.");

        UserLogin.UserLoginType loginType = supportedLoginType(params.get("signtype"));
        if (loginType == null) return new ContentResponse(
                400,
                ContentResponse.Content.VERIFY_PAGE,
                Optional.ofNullable(oauthSessionId))
                .withParam("message", "Choose a signup type");

        String externalcontact = params.get("externalcontact");
        if (externalcontact != null) externalcontact = loginType.normalize(externalcontact);
        if (!loginType.isValid(externalcontact)) return new ContentResponse(
                400,
                ContentResponse.Content.VERIFY_PAGE,
                Optional.ofNullable(oauthSessionId))
                .withParam("message", "Invalid " + loginType.name());

        UserLogin login = config.userLoginRepository().read(UserLogin.id(loginType, externalcontact));
        if(login == null) return new ContentResponse(
                400,
                ContentResponse.Content.SIGNUP_PAGE,
                Optional.ofNullable(oauthSessionId))
                .withParam("message", "Not Registered. Please sign up.");

        if (login.isVerified()) return new ContentResponse(
                400,
                ContentResponse.Content.LOGIN_PAGE,
                Optional.ofNullable(oauthSessionId))
                .withParam("message", "Already Verified. Please log in.");

        LocalDateTime now = utcNow();
        if (login.getTokenExpires() == null || login.getTokenExpires().isBefore(now)) {
            return new ContentResponse(
                    400,
                    ContentResponse.Content.VERIFY_PAGE,
                    Optional.ofNullable(oauthSessionId))
                    .withParam("message", "Code Expired. TODO: Reissue");
        }
        String code = params.get("code");
        code = code == null ? "" : code.trim();
        if (!code.equals(login.getTokenValue())) return new ContentResponse(
                400,
                ContentResponse.Content.VERIFY_PAGE,
                Optional.ofNullable(oauthSessionId))
                .withParam("message", "Incorrect code");

        login.setVerified(true);
        login.setTokenValue(null);
        login.setTokenExpires(null);
        login.setTokenIssued(null);
        config.userLoginRepository().update(login);

        AuthRequest authRequest = config.authRequestRepository().read(AuthRequest.id(oauthSessionId));
        if (authRequest == null) {
            return new ContentResponse(
                    400,
                    ContentResponse.Content.ERROR_PAGE,
                    Optional.of(oauthSessionId))
                    .withParam("error", "OIDC Auth Request Timed Out or Not Present");
        }

        // TODO: Handle session does not exist (!)
            authenticate(oauthSessionId, login.getRef());
            return redirectAfterSuccessfulAuth(oauthSessionId, authRequest, login.getRef());
    }

    private OauthSession authenticate(String oauthSessionId, EntityId userId) {
        long sessionDuration = TimeUnit.SECONDS.toSeconds(300);
        OauthSession session = config.oauthSessionRepository().read(OauthSession.id(oauthSessionId));
        if (session != null) {
            session.setRef(userId);
            session.setExpiry(utcNow().plusSeconds(sessionDuration));
            config.oauthSessionRepository().update(session);
        }
        return session;
    }

    private UserLogin.UserLoginType supportedLoginType(String signtype) {
        if(signtype == null || signtype.isBlank()) return null;
        UserLogin.UserLoginType loginType = null;
        try {
            loginType = UserLogin.UserLoginType.valueOf(signtype.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }

        // phone is currently disabled
        switch (loginType) {
            case phone -> {
                return null;
            }
        }
        return loginType;
    }

    public OperationResponse login(Map<String, String> params, String oauthSessionId) {
        if (oauthSessionId == null || oauthSessionId.isBlank()) return new ContentResponse(
                400,
                ContentResponse.Content.ERROR_PAGE,
                Optional.ofNullable(oauthSessionId))
                .withParam("error", "No OAuth session in progress");
//        if (oauthSessionId == null) throw new NullPointerException("Must have a session ID");
//        OauthSession session = null; //config.oauthSessionRepository().lookup(oauthSessionId);

        if (params.isEmpty() || !params.containsKey("password")) // just display login page with no attempts at anything else
            return new ContentResponse(
                    200,
                    ContentResponse.Content.LOGIN_PAGE,
                    Optional.of(oauthSessionId))
                    .withParam("acceptingSignup", Boolean.toString(acceptingSignup()));


        // its a login attempt
        String password = params.get("password");
        String externalcontact = params.get("externalcontact");

        // TODO
        UserLogin login = config.userLoginRepository().read(UserLogin.id(UserLogin.UserLoginType.email, externalcontact));
        User user = null;
        if(login != null)  {
            user = config.userRepository().read(login.getRef());
            boolean matches = PasswordUtils.matches(user.getSalt(), user.getPassword(), password);
            if (!matches) user = null;
        }

//        https://openid.net/specs/openid-connect-core-1_0.html#AuthResponse
        if (user != null) {
            OauthSession session = authenticate(oauthSessionId, user.getId());
//            session = config.oauthSessionRepository().authenticate(oauthSessionId, "endUser.get().userId()");

            AuthRequest authRequest = config.authRequestRepository().read(AuthRequest.id(oauthSessionId));
            if (authRequest == null) {
                return new ContentResponse(
                        400,
                        ContentResponse.Content.ERROR_PAGE,
                        Optional.of(oauthSessionId))
                        .withParam("error", "OIDC Auth Request Timed out");
            }
            return redirectAfterSuccessfulAuth(oauthSessionId, authRequest, user.getId());
        } else {
            return new ContentResponse(
                    200,
                    ContentResponse.Content.LOGIN_PAGE,
                    Optional.of(oauthSessionId))
                    .withParam("acceptingSignup", Boolean.toString(acceptingSignup()))
                    .withParam("message", "Authorization Failed - Please try again");
        }
    }


    boolean acceptingSignup() {
        return Boolean.valueOf(ConfigParameters.signupEnabled.get(config.parameterRepository()));
    }

    private OperationResponse redirectAfterSuccessfulAuth(
            String oauthSessionId,
            AuthRequest authRequest,
            EntityId userId
    ) {
        try {
            // redirect back to app.
            //
            // Potential option - use an interposing screen.
            // Use case - ignoring redirect URI and using this service
            // as an 'index' service
            // eg: these services have been authorized
            //   - app1
            //   - app2

            String redirectUri = authRequest.getRedirectUri();

            if (!redirectUri.endsWith("?")) {
                redirectUri = redirectUri + "?";
            }

            Authcode authCode = new Authcode();
            authCode.setId(Authcode.id(UUID.randomUUID().toString()));
            authCode.setRef(userId);
            authCode.setOauthSessionId(oauthSessionId);
            authCode.setExpiry(utcNow().plusMinutes(5));
            config.authcodeRepository().create(authCode);
            redirectUri = redirectUri + "code=" + URLEncoder.encode(authCode.getId().value, "UTF8");

            Optional<String> state = authRequest.getState();
            if (state.isPresent()) redirectUri = redirectUri + "&state=" + URLEncoder.encode(state.get(), "UTF8");

            return new RedirectResponse(redirectUri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
