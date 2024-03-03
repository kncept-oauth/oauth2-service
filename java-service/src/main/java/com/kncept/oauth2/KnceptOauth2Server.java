package com.kncept.oauth2;

import com.kncept.oauth2.config.EnvPropertyConfiguration;
import com.kncept.oauth2.config.InMemoryConfiguration;
import com.kncept.oauth2.config.Oauth2StorageConfiguration;
import com.kncept.oauth2.config.client.Client;
import com.kncept.oauth2.operation.response.ContentResponse;
import com.kncept.oauth2.operation.response.OperationResponse;
import com.kncept.oauth2.operation.response.RedirectResponse;
import com.kncept.oauth2.operation.response.RenderedContentResponse;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class KnceptOauth2Server implements HttpHandler {
    private static final String knceptClient = "kncept-oidc-client";

    public static void main(String[] args) throws IOException {
        HttpServer server;

        // localhost or 127.0.0.1
        String hostname = System.getProperty("hostname", "localhost");
//        String hostname = System.getProperty("hostname", "127.0.0.1");
        int port = Integer.parseInt(System.getProperty("port", "8080"));

        String mode = System.getProperty("mode", "http").toLowerCase();

        String hostedUrl = mode + "://" + hostname + ":" + port;

        if (mode.equals("http")) {
            server = HttpServerProvider.provider().createHttpServer(new InetSocketAddress(hostname, port), 0);
        } else if (mode.equals("https")) {
            server = HttpServerProvider.provider().createHttpsServer(new InetSocketAddress(hostname, port), 0);
//            ((HttpsServer)server).setHttpsConfigurator();
        } else throw new RuntimeException("Unknown mode: " + mode);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.createContext("/", new KnceptOauth2Server(hostedUrl));

        server.start();
        System.out.println("started on " + mode + "://" + hostname + ":" + port + "/");
    }

    private final Oauth2Processor oauth2;
    private final Oauth2AutoRouter router;

    public KnceptOauth2Server(String hostedUrl) {
        Oauth2StorageConfiguration config = EnvPropertyConfiguration.loadStorageConfigFromEnvProperty();

        if(config.clientRepository().read(Client.id(knceptClient)) == null) {
            Client knceptOidcClient = new Client();
            knceptOidcClient.setId(Client.id(knceptClient));
            knceptOidcClient.setEnabled(true);
            knceptOidcClient.setSecret(knceptClient);
            config.clientRepository().create(knceptOidcClient);
        }
        oauth2 = new Oauth2(config, hostedUrl);
        oauth2.init(true); // easier init of tables
        router = new Oauth2AutoRouter(oauth2);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().toLowerCase();
        Map<String, String> cookies = headerCookies(exchange);
        Optional<String> oauthSessionId = Optional.ofNullable(cookies.get("oauthSessionId"));
//            Optional<String> jwt = Optional.ofNullable(cookies.get("jwt"));
           System.out.println("PATH " + path + "  " + oauthSessionId.orElse("_"));
        OperationResponse response = router.route(path, exchange.getRequestMethod(), bodyOrQueryParams(exchange), cookies.get("oauthSessionId"));
        handleResponse(exchange, response);
    }

    private void handleResponse(HttpExchange exchange, OperationResponse response) throws IOException {
        if (response.isRenderedContentResponse()) handleResponse(exchange, response.asRenderedContentResponse());
        else if (response.isContent()) handleResponse(exchange, response.asContent());
        else if (response.isRedirect()) handleResponse(exchange, response.asRedirect());
        else throw new IllegalStateException("Unknown response type");
    }
    private void handleResponse(HttpExchange exchange, RenderedContentResponse response) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();

        // useful link for breaking cors: https://github.com/kncept/slog/blob/main/backend/src/router.ts
        responseHeaders.put("Access-Control-Allow-Headers", asList(
                "Cookie",
                "Authorization"
        ));
        String accessControlAllowOrigin = "*";
        if (exchange.getRequestHeaders().containsKey("Origin")) accessControlAllowOrigin = exchange.getRequestHeaders().getFirst("Origin");

//        for(String requestHeaderName: exchange.getRequestHeaders().keySet())
//            System.out.println("requestHeaderName " + requestHeaderName + "  " + exchange.getRequestHeaders().get(requestHeaderName));

        responseHeaders.add("Access-Control-Allow-Origin", accessControlAllowOrigin);
        responseHeaders.put("Access-Control-Allow-Methods", asList("GET", "POST")); // GET and POST
        responseHeaders.add("Access-Control-Allow-Credentials", "true");


        response.oauthSessionId().ifPresent(oauthSessionId -> {
            responseHeaders.add("Set-Cookie", "oauthSessionId=" + oauthSessionId + "; HttpOnly");
        });
        response.headers().forEach(responseHeaders::add);
        exchange.sendResponseHeaders(response.responseCode(), 0);
        if (response.base64Encoded()) {
            exchange.getResponseBody().write(Base64.getDecoder().decode(response.content()));
        } else {
            exchange.getResponseBody().write(response.content().getBytes());
        }
        exchange.getResponseBody().close();
    }
    private void handleResponse(HttpExchange exchange, ContentResponse response) throws IOException {
        handleResponse(exchange, oauth2.render(response));
    }
    private void handleResponse(HttpExchange exchange, RedirectResponse response) throws IOException {
        exchange.getResponseHeaders().add("Location", response.redirectUri());
        exchange.sendResponseHeaders(302, -1);
    }


    private Map<String, String> headerCookies(HttpExchange exchange) {
        Map<String, String> cookies = new HashMap<>();
        List<String> cookieHeaders = exchange.getRequestHeaders().get("Cookie");
        if (cookieHeaders != null) for(String cookieString: cookieHeaders) {
            for(String cookie: cookieString.split(";")) {
                String[] nvp = cookie.trim().split("=");
                if (nvp.length == 2) cookies.put(nvp[0], nvp[1]); // hacky ignore malformed coookies
            }
        }
        return cookies;
    }

    private Map<String, String> bodyOrQueryParams(HttpExchange exchange) throws IOException {
        return exchange.getRequestMethod().toLowerCase().equals("post") ?
                bodyParams(exchange) : queryParams(exchange);
    }
    private Map<String, String> queryParams(HttpExchange exchange) throws IOException {
        return extractSimpleParams(exchange.getRequestURI().getQuery());
    }
    private Map<String, String> bodyParams(HttpExchange exchange) throws IOException {
        return extractSimpleParams(new String(exchange.getRequestBody().readAllBytes()));
    }
    // based on https://stackoverflow.com/questions/1667278/parsing-query-strings-on-android
    public static Map<String, String> extractSimpleParams(String query) throws IOException {
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

