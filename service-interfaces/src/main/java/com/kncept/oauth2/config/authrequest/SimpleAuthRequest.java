package com.kncept.oauth2.config.authrequest;

import java.util.Optional;

public class SimpleAuthRequest implements AuthRequest {
    private final String oauthSessionId;
    private final Optional<String> state;
    private final Optional<String> nonce;
    private final String redirectUri;
    private final String clientId;
    private final String responseType;
    private final long expiryTime;

    public SimpleAuthRequest(
            String oauthSessionId,
            Optional<String> state,
            Optional<String> nonce,
            String redirectUri,
            String clientId,
            String responseType,
            long expiryTime
    ) {
        this.oauthSessionId = oauthSessionId;
        this.state = state;
        this.nonce = nonce;
        this.redirectUri = redirectUri;
        this.clientId = clientId;
        this.responseType = responseType;
        this.expiryTime = expiryTime;
    }

    @Override
    public Optional<String> state() {
        return state;
    }

    @Override
    public String redirectUri() {
        return redirectUri;
    }

    @Override
    public String clientId() {
        return clientId;
    }

    @Override
    public String responseType() {
        return responseType;
    }

    @Override
    public String oauthSessionId() {
        return oauthSessionId;
    }

    @Override
    public Optional<String> nonce() {
        return nonce;
    }

    @Override
    public long expiryTime() {
        return expiryTime;
    }
}