package com.kncept.oauth2.config.authcode;

public class SimpleAuthcode implements Authcode {
    private final String authCode;
    private final String oauthSessionId;


    public SimpleAuthcode(
            String authCode,
            String oauthSessionId
    ) {
        this.authCode = authCode;
        this.oauthSessionId = oauthSessionId;
    }
    @Override
    public String oauthSessionId() {
        return oauthSessionId;
    }

    @Override
    public String authCode() {
        return authCode;
    }
}
