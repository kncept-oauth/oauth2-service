package com.kncept.oauth2;

import com.kncept.oauth2.config.Oauth2StorageConfiguration;
import com.kncept.oauth2.config.parameter.ConfigParameters;
import com.kncept.oauth2.content.HtmlPageVendor;
import com.kncept.oauth2.crypto.key.KeyManager;
import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RenderedContentResponse;
import com.kncept.oauth2.subhandler.*;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static com.kncept.oauth2.util.JsonUtils.toJson;
import static com.kncept.oauth2.util.ParamUtils.optional;
import static com.kncept.oauth2.util.ParamUtils.required;

public class Oauth2 implements Oauth2Processor {

    private static Logger logger = Logger.getLogger(Oauth2.class.getName());

    private final Oauth2StorageConfiguration config;
    private final KeyManager keyManager;
    private final HtmlPageVendor htmlPageVendor;

    private final String hostedUrl;

    public Oauth2(Oauth2StorageConfiguration config, String hostedUrl) {
        this.config = config;
        this.hostedUrl = hostedUrl.endsWith("/") ? hostedUrl : hostedUrl + "/";
        keyManager = new KeyManager(config);
        htmlPageVendor = new HtmlPageVendor(this.hostedUrl);
    }

    // https://auth0.com/docs/get-started/applications/configure-applications-with-oidc-discovery
    // should be on a https://YOUR_DOMAIN(and optional prefix root)/.well-known/openid-configuration URL
    @Override
    public RenderedContentResponse openIdDiscovery() {
        // WIP
        return new DiscoveryHandler(config, hostedUrl).openIdDiscovery();
    }

    @Override
    public RenderedContentResponse oauthDiscovery() {
        return new DiscoveryHandler(config, hostedUrl).oauthDiscovery();
    }

    @Override
    public RenderedContentResponse jwks() {
        return new RenderedContentResponse(
                200,
                toJson(keyManager.jwks()),
                "application/json",
                Optional.empty(),
                false
        );
    }
    @Override
    public RenderedContentResponse pkcs8() {
        return new RenderedContentResponse(
                200,
                toJson(keyManager.pkcs8()),
                "application/json",
                Optional.empty(),
                false
        );
    }

    // https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
    @Override
    public OperationResponse authorize(Map<String, String> params, Optional<String> oauthSessionId) {
        return new AuthorizeHandler(config).authorize(params, oauthSessionId);
    }

    @Override
    public OperationResponse signup(Map<String, String> params, String oauthSessionId) {
        return new LoginSignupHandler(config).signup(params, oauthSessionId);
    }

    @Override
    public OperationResponse verify(Map<String, String> params, String oauthSessionId) {
        return new LoginSignupHandler(config).verify(params, oauthSessionId);
    }

    @Override
    public OperationResponse login(Map<String, String> params, String oauthSessionId) {
        return new LoginSignupHandler(config).login(params, oauthSessionId);
    }

    // https://openid.net/specs/openid-connect-core-1_0.html#TokenEndpoint
    @Override
    public RenderedContentResponse token(Map<String, String> params) {
        return new TokenHandler(config, keyManager, hostedUrl).token(params);
    }

    @Override
    public void init(boolean await) {
        new InitHandler(config).init(await);
    }

    @Override public RenderedContentResponse renderCss() {
        return render(new ContentResponse(200, ContentResponse.Content.CSS, Optional.empty()));
    }
    @Override public RenderedContentResponse render(ContentResponse response) {
        return new RenderedContentResponse(response, renderContentToString(response), response.content() == ContentResponse.Content.CSS ? "text/css" : "text/html", false);
    }
    public String renderContentToString(ContentResponse response) {
        Map<String, String> params = response.params();
        switch(response.content()) {
            case SIGNUP_PAGE -> {
                boolean acceptingSignup = Boolean.valueOf(ConfigParameters.signupEnabled.get(config.parameterRepository()));
                if (acceptingSignup) return htmlPageVendor.signupPage(optional("message", params));
            }
            case VERIFY_PAGE -> {
                return htmlPageVendor.verifyPage(optional("maessage", params));
            }
            case LOGIN_PAGE -> {
                boolean acceptingSignup = Boolean.valueOf(ConfigParameters.signupEnabled.get(config.parameterRepository()));
                return htmlPageVendor.loginPage(optional("message", params), acceptingSignup);
            }
            case PROFILE_PAGE -> {
                return htmlPageVendor.profilePage(params);
            }
            case ERROR_PAGE -> {
                return htmlPageVendor.errorPage(
                        required("error", params)
                );
            }
            case CSS -> {
                return htmlPageVendor.css();
            }
        }
        throw new IllegalStateException("Unknown response content: " + response.content());
    }


}
