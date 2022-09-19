package com.kncept.oauth2;

import com.kncept.oauth2.config.Oauth2Configuration;
import com.kncept.oauth2.content.HtmlPageVendor;
import com.kncept.oauth2.crypto.KeyVendor;
import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RenderedContentResponse;
import com.kncept.oauth2.subhandler.AuthorizeHandler;
import com.kncept.oauth2.subhandler.InitHandler;
import com.kncept.oauth2.subhandler.LoginSignupHandler;
import com.kncept.oauth2.subhandler.TokenHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static com.kncept.oauth2.util.ParamUtils.optional;
import static com.kncept.oauth2.util.ParamUtils.required;

public class Oauth2 implements Oauth2Processor {

    private static Logger logger = Logger.getLogger(Oauth2.class.getName());

    private final Oauth2Configuration config;
    private final KeyVendor keyVendor = new KeyVendor();
    private final HtmlPageVendor htmlPageVendor = new HtmlPageVendor();

    public Oauth2(Oauth2Configuration config) {
        this.config = config;
    }

    // https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
    public OperationResponse authorize(Map<String, String> params, Optional<String> oauthSessionId) throws IOException {
        return new AuthorizeHandler(config).authorize(params, oauthSessionId);
    }

    public OperationResponse login(Map<String, String> params, String oauthSessionId) throws IOException {
        return new LoginSignupHandler(config).login(params, oauthSessionId);
    }

    public OperationResponse signup(Map<String, String> params, String oauthSessionId) throws IOException {
        return new LoginSignupHandler(config).signup(params, oauthSessionId);
    }

    // https://openid.net/specs/openid-connect-core-1_0.html#TokenEndpoint
    public RenderedContentResponse token(Map<String, String> params) {
        return new TokenHandler(config, keyVendor).token(params);
    }

    public void init(boolean await) {
        new InitHandler(config).init(await);
    }

    public RenderedContentResponse renderCss() {
        return render(new ContentResponse(200, ContentResponse.Content.CSS, Optional.empty()));
    }
    public RenderedContentResponse render(ContentResponse response) {
        return new RenderedContentResponse(response, renderContentToString(response), response.content() == ContentResponse.Content.CSS ? "text/css" : "text/html", false);
    }
    public String renderContentToString(ContentResponse response) {
        Map<String, String> params = response.params();
        switch(response.content()) {
            case ERROR_PAGE -> {
                return htmlPageVendor.errorPage(
                        required("error", params)
                );
            }
            case LOGIN_PAGE -> {
                return htmlPageVendor.loginPage(
                        optional("message", params),
                        Boolean.parseBoolean(optional("acceptingSignup", params).orElse(null))
                );
            }
            case SIGNUP_PAGE -> {
                return htmlPageVendor.signupPage(optional("message", params));
            }
            case CSS -> {
                return htmlPageVendor.css();
            }
        }
        throw new IllegalStateException("Unknown response content: " + response.content());
    }


}
