package com.kncept.oauth2.config.session;

import java.util.Optional;

public class SimpleOauthSession implements OauthSession {
    private final String oauthSessionId;
    private final Optional<String> userId;

    public SimpleOauthSession(
            String oauthSessionId,
            Optional<String> userId
    ) {
        this.oauthSessionId = oauthSessionId;
        this.userId = userId;
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
}