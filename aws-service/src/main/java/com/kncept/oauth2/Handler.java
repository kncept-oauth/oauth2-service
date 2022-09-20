package com.kncept.oauth2;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.kncept.oauth2.config.Oauth2Configuration;
import com.kncept.oauth2.config.SystemProperyConfiguration;
import com.kncept.oauth2.config.client.Client;
import com.kncept.oauth2.config.client.SimpleClient;
import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RedirectResponse;
import com.kncept.oauth2.operation.response.RenderedContentResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.logging.Logger;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = Logger.getLogger(Handler.class.getName());
    private static final String knceptClient = "kncept-oidc-client";
    private final Oauth2Processor oauth2;
    public Handler() {
        this(Oauth2Configuration.loadConfigurationFromEnvProperty(
                () -> new SystemProperyConfiguration()));
    }
    public Handler(Oauth2Configuration config) {
        oauth2 = new Oauth2(config);
        oauth2.init(false); // easier init of tables
        if(config.clientRepository().lookup(knceptClient).isEmpty()) {
            Client knceptOidcClient = new SimpleClient(knceptClient, true);
            config.clientRepository().update(knceptOidcClient);
        }
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            String path = input.getPath();
            path = path == null ? "" : path.toLowerCase();
            Map<String, String> cookies = headerCookies(input);
            Optional<String> oauthSessionId = Optional.ofNullable(cookies.get("oauthSessionId"));
            if (path.equals("/authorize") || path.equals("/oauth/authorize")) {
                return handleResponse(oauth2.authorize(bodyOrQueryParams(input), oauthSessionId));
            } else if (path.equals("/login")) {
                return handleResponse(oauth2.login(bodyParams(input), oauthSessionId.orElseThrow()));
            } else if (path.equals("/signup")) {
                return handleResponse(oauth2.signup(bodyParams(input), oauthSessionId.orElseThrow()));
            } else if (path.equals("/style.css")) {
                return handleResponse(oauth2.renderCss());
            } else if (path.equals("/token") || path.equals("/oauth/token")) {
                return handleResponse(oauth2.token(bodyOrQueryParams(input)));
            } else if (path.equals("/access_token") || path.equals("/oauth/access_token")) {
                return handleResponse(oauth2.token(bodyOrQueryParams(input)));
            } else if (path.equals("/init")) {
                oauth2.init(true);
                return emptyResponse(200);
            } else {
                return emptyResponse(404);
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return response(e.getMessage(), 500);
        }
    }

    private APIGatewayProxyResponseEvent handleResponse(OperationResponse response) {
        if (response.isRenderedContentResponse()) return handleResponse(response.asRenderedContentResponse());
        else if (response.isContent()) return handleResponse(response.asContent());
        else if (response.isRedirect()) return handleResponse(response.asRedirect());
        else throw new IllegalStateException("Unknown response type");
    }
    private APIGatewayProxyResponseEvent handleResponse(RenderedContentResponse response) {
        Map<String, String> headers = new TreeMap<>();
        response.oauthSessionId().ifPresent(oauthSessionId -> {
            headers.put("Set-Cookie", "oauthSessionId=" + oauthSessionId + "; HttpOnly");
        });
        headers.putAll(response.headers());
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setHeaders(headers);
        responseEvent.setIsBase64Encoded(response.base64Encoded());
        responseEvent.setBody(response.content());
        responseEvent.setStatusCode(response.responseCode());
        return responseEvent;
    }
    private APIGatewayProxyResponseEvent handleResponse(ContentResponse response) {
        return handleResponse(oauth2.render(response));
    }
    private APIGatewayProxyResponseEvent handleResponse(RedirectResponse response) {
        Map<String, String> headers = new TreeMap<>();
        headers.put("Location", response.redirectUri());
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setHeaders(headers);
        responseEvent.setStatusCode(response.responseCode());
        return responseEvent;
    }
    private APIGatewayProxyResponseEvent response(String body, int statusCode) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setStatusCode(statusCode);
        if (body == null) body = "";
        responseEvent.setBody(body);
        if (body.equals("")) {
            responseEvent.setHeaders(new TreeMap<>());
            responseEvent.getHeaders().put("Content-Type", "text/plain");
        }
        return responseEvent;
    }
    private APIGatewayProxyResponseEvent emptyResponse(int statusCode) {
        return response("", statusCode);
    }

    private Map<String, String> headerCookies(APIGatewayProxyRequestEvent input) {
        Map<String, String> cookies = new HashMap<>();
        if (input.getMultiValueHeaders() == null) return cookies;
        List<String> cookieHeaders = input.getMultiValueHeaders().get("Cookie");
        if (cookieHeaders != null) for(String cookieString: cookieHeaders) {
            for(String cookie: cookieString.split(";")) {
                String[] nvp = cookie.trim().split("=");
                if (nvp.length == 2) cookies.put(nvp[0], nvp[1]); // hacky ignore malformed coookies
            }
        }
        return cookies;
    }

    private Map<String, String> bodyOrQueryParams(APIGatewayProxyRequestEvent input) throws IOException {
        return input.getHttpMethod().toLowerCase().equals("post") ?
                bodyParams(input) : queryParams(input);
    }
    private Map<String, String> queryParams(APIGatewayProxyRequestEvent input) {
        Map<String, String> params = input.getQueryStringParameters();
        if (params == null) params = new HashMap<>();
        return params;
    }
    private Map<String, String> bodyParams(APIGatewayProxyRequestEvent input) throws IOException {
        return extractSimpleParams(input.getBody());
    }
    public Map<String, String> extractSimpleParams(String query) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        if (query != null) for (String param : query.split("&")) {
            String pair[] = param.split("=");
            String key = URLDecoder.decode(pair[0], "UTF-8");
            String value = "";
            if (pair.length > 1) {
                value = URLDecoder.decode(pair[1], "UTF-8");
            }
            params.put(key, value);
        }
        return params;
    }
}
