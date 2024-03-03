package com.kncept.oauth2;

import com.kncept.oauth2.config.InMemoryConfiguration;
import com.kncept.oauth2.config.Oauth2StorageConfiguration;
import com.kncept.oauth2.config.client.Client;
import com.kncept.oauth2.config.user.User;
import com.kncept.oauth2.config.user.UserLogin;
import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RedirectResponse;
import com.kncept.oauth2.operation.response.RenderedContentResponse;
import com.kncept.oauth2.subhandler.AuthorizeHandler;
import com.kncept.oauth2.util.PasswordUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

import static com.kncept.oauth2.util.DateUtils.utcNow;

public class AuthorizeFlowTest {

    Oauth2 processor = new Oauth2(new InMemoryConfiguration(), "");

    // todo: add a 'with pkce' test as well
    @Test
    public void implicitFlow() {
        Client oidcClient = createOidcClient();
        ContentResponse authorizeResponse = doAuthorize(oidcClient);

        RedirectResponse redirectBackToAppResponse = doLogin(authorizeResponse);
        String code = extractCodeFromRedirectBackToAppResponse(redirectBackToAppResponse);


        Map<String, String> tokensRequestParams = new HashMap<>();
        tokensRequestParams.put("grant_type", "TODO");
        tokensRequestParams.put("code", code);
        OperationResponse tokenResponse = processor.token(tokensRequestParams);
        if (tokenResponse.isRenderedContentResponse()) {
            RenderedContentResponse rcr = tokenResponse.asRenderedContentResponse();
            System.out.println(rcr.headers());
            System.out.println(rcr.content());
        }
        // TODO: Validate token !!
    }

    ContentResponse doAuthorize(Client oidcClient) {
        Map<String, String> authorizeRequestParams = new HashMap<>();
        authorizeRequestParams.put("scope", "openid"); //
        authorizeRequestParams.put("response_type", AuthorizeHandler.ResponseType.code.name());
        authorizeRequestParams.put("client_id", oidcClient.getId().toString());
        authorizeRequestParams.put("redirect_uri", "http://localhost/redirect-back");

        // also PKCE is required (!!)

//        Optional<String> state = optional("state", params);
//        Optional<String> nonce = optional("nonce", params);

        // send an authorize request
        OperationResponse authorizeResponse = processor.authorize(authorizeRequestParams, Optional.empty());
        Assertions.assertEquals(200, authorizeResponse.responseCode());
        if (authorizeResponse.isContent()) {
            return authorizeResponse.asContent();
        }
        throw new IllegalStateException("Error in Auth Flow");
    }
    RedirectResponse doLogin(ContentResponse authorizeResponse) {
            if(authorizeResponse.content() == ContentResponse.Content.LOGIN_PAGE) {
                User user = createUserWithLogin();
                // username/salt for email/password
                Map<String, String> loginParams = new HashMap<>();
                loginParams.put("externalcontact", user.getUsername());
                loginParams.put("password", user.getSalt());

                OperationResponse loginResponse = processor.login(loginParams, authorizeResponse.oauthSessionId().orElse(null));
                if (loginResponse.isRedirect()) { // expect a redirect back
                    return loginResponse.asRedirect();
                }
            }
        throw new IllegalStateException("Login Flow Errored");
    }

    String extractCodeFromRedirectBackToAppResponse(RedirectResponse redirectBackToAppResponse) {
        String code;
        try {
            URI redirectUri = URI.create(redirectBackToAppResponse.redirectUri());
            Map<String, String> simpleParams = splitQuery(redirectUri.toURL());
            return simpleParams.get("code");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Client createOidcClient() {
        Client newClient = new Client();
        newClient.setId(Client.id(UUID.randomUUID().toString()));
        newClient.setSecret(UUID.randomUUID().toString());
        newClient.setEnabled(true);
        newClient.setEndpoints(new String[]{"*"});
        processor.getConfig().clientRepository().create(newClient);
        return newClient;
    }

    private User createUserWithLogin() {
        User user = new User();
        user.setId(User.id());
        user.setWhen(utcNow());
        user.setUsername("user@" + System.currentTimeMillis());
        user.setSalt(PasswordUtils.generateSalt());
        user.setPassword(PasswordUtils.hash(user.getSalt(), user.getSalt()));
        processor.getConfig().userRepository().create(user);

        UserLogin login = new UserLogin();
        login.setId(UserLogin.id(UserLogin.UserLoginType.email, user.getUsername()));
        login.setRef(user.getId());
        login.setVerified(true);
        processor.getConfig().userLoginRepository().create(login);

        return user;
    }

    // https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection
    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

}
