package com.kncept.oauth2.config.authcode;

public class SimpleAuthcode implements Authcode {
    private final String authCode;
    private final String oauthSessionId;
    private final long expiryTime;

    public SimpleAuthcode(
            String authCode,
            String oauthSessionId,
            long expiryTime
    ) {
        this.authCode = authCode;
        this.oauthSessionId = oauthSessionId;
        this.expiryTime = expiryTime;
    }
    @Override
    public String oauthSessionId() {
        return oauthSessionId;
    }

    @Override
    public String authCode() {
        return authCode;
    }

    @Override
    public long expiryTime() {
        return expiryTime;
    }
}
