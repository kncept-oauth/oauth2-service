package com.kncept.oauth2.config.session;

import java.util.Optional;

public class SimpleOauthSession implements OauthSession {
    private final String oauthSessionId;
    private final Optional<String> userId;
    private final long expiryTime;

    public SimpleOauthSession(
            String oauthSessionId,
            Optional<String> userId,
            long expiryTime
    ) {
        this.oauthSessionId = oauthSessionId;
        this.userId = userId;
        this.expiryTime = expiryTime;
    }

    @Override
    public String oauthSessionId() {
        return oauthSessionId;
    }

    @Override
    public boolean authenticated() {
        return userId.isPresent();
    }

    @Override
    public Optional<String> userId() {
        return userId;
    }

    @Override
    public long expiryTime() {
        return expiryTime;
    }

    public SimpleOauthSession expiryTime(long expiryTime) {
        return new SimpleOauthSession(oauthSessionId, userId, expiryTime);
    }
}