package com.kncept.oauth2;

import com.kncept.oauth2.operation.response.OperationResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class KnceptOauth2Server implements HttpHandler {

    public static void main(String[] args) throws IOException {

        HttpServer server;

        // localhost or 127.0.0.1
        String hostname = System.getProperty("hostname", "localhost");
        int port = Integer.parseInt(System.getProperty("port", "8080"));

        String mode = System.getProperty("mode", "http");
        if (mode.toLowerCase().equals("http")) {
            server = HttpServerProvider.provider().createHttpServer(new InetSocketAddress(hostname, port), 0);
        } else if (mode.toLowerCase().equals("https")) {
            server = HttpServerProvider.provider().createHttpsServer(new InetSocketAddress(hostname, port), 0);
//            ((HttpsServer)server).setHttpsConfigurator();
        } else throw new RuntimeException("Unknown mode: " + mode);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.createContext("/", new KnceptOauth2Server());

        server.start();
        System.out.println("started " + hostname + ":" + port + " (" + mode + ")");

        // shouldn't need this, but allow it to be switched if you must
        if (System.getProperties().containsKey("wait")) {
            synchronized (server) {
                try {
                    server.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Error waiting for server", e);
                }
            }
        }

    }

    private Oauth2 oauth2;

    public KnceptOauth2Server() throws IOException {
        oauth2 = new Oauth2();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath().toLowerCase();

            if (
                    path.equals("/authorize")
            ) {
                OperationResponse response = oauth2.authorize(getUrlParameters(exchange.getRequestURI()));
                handleResponse(exchange, response);
                System.out.println("did auth");
            } else             if (
                    path.equals("/login")
            ) {
                Map<String, String> params = new HashMap<>();
                InputStream in = exchange.getRequestBody();
                String value = new String(in.readAllBytes());
                System.out.println("body: " + value);
                handleResponse(exchange, oauth2.login(params));
            } else {
                System.out.println("" + exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
                exchange.sendResponseHeaders(200, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleResponse(HttpExchange exchange, OperationResponse response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", response.contentType);

        if (response.contentType.equalsIgnoreCase("text/html")) {
            exchange.sendResponseHeaders(response.responseCode, 0);
            exchange.getResponseBody().write(response.responseDetail.getBytes());
            exchange.getResponseBody().close();
        } else {
            throw new IllegalStateException("Unknown content type: " + response.contentType);
        }

    }

    // based on https://stackoverflow.com/questions/1667278/parsing-query-strings-on-android
    public static Map<String, String> getUrlParameters(URI uri)
            throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<String, String>();
        if (uri.getQuery() != null) for (String param : uri.getQuery().split("&")) {
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

