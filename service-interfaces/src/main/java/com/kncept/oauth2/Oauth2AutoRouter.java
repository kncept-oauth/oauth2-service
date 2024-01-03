package com.kncept.oauth2;

import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RedirectResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Oauth2AutoRouter {

    private final Oauth2Processor oauth2;
    public Oauth2AutoRouter(Oauth2Processor oauth2) {
        this.oauth2 = oauth2;
    }

    public OperationResponse route(
            String path,
            String method,
            Map<String, ?> params,
            String oauthSessionIdCookie
    ) {
        Optional<String> oauthSessionId = Optional.ofNullable(oauthSessionIdCookie);

        Map<String, String> stringEntityMap = new HashMap<>();
        if (params != null) for(Map.Entry<String, ?> entry: params.entrySet()) {
            if (entry.getValue() != null) {
                stringEntityMap.put(entry.getKey(), entry.getValue().toString());
            }
        }
        path = path == null ? "" : path.toLowerCase();
        if (!path.startsWith("/")) path = "/" + path;
        try {
            if (path.equals("/")) {
                return new RedirectResponse("/.well-known/openid-configuration");
            } else if (path.equals("/authorize")) {
                return oauth2.authorize(stringEntityMap, oauthSessionId);
            } else if (path.equals("/login")) {
                return oauth2.login(stringEntityMap, oauthSessionId.orElse(null));
            } else if (path.equals("/signup")) {
                return oauth2.signup(stringEntityMap, oauthSessionId.orElse(null));
            } else if (path.equals("/verify")) {
                return oauth2.verify(stringEntityMap, oauthSessionId.orElse(null));
            } else if (path.equals("/style.css")) {
                return oauth2.renderCss();
            } else if (path.equals("/token") || path.equals("/oauth/token")) {
                return oauth2.token(stringEntityMap);
            } else if (path.equals("/.well-known/openid-configuration")) {
                return oauth2.openIdDiscovery();
            } else if (path.equals("/.well-known/oauth-authorization-server")) {
                return oauth2.oauthDiscovery();
            } else if (path.equals("/jwks")) {
                return oauth2.jwks();
            } else if (path.equals("/pkcs8")) {
                return oauth2.pkcs8();
            } else {
                return new ContentResponse(
                        404,
                        ContentResponse.Content.ERROR_PAGE,
                        oauthSessionId
                ).withParam("error", "Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ContentResponse(
                    500,
                    ContentResponse.Content.ERROR_PAGE,
                    oauthSessionId
            ).withParam("error", e.toString());
        }
    }

}
