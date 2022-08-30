package com.kncept.oauth2.authrequest;

public class SimpleAuthRequest implements AuthRequest {

    final String state; // nullable
    final String redirectUri;
    final String clientId;

    public SimpleAuthRequest(
            String state,
            String redirectUri,
            String clientId
    ){
        this.state = state;
        this.redirectUri = redirectUri;
        this.clientId = clientId;
    }

    public SimpleAuthRequest(
            String redirectUri,
            String clientId
    ){
        this.state = null;
        this.redirectUri = redirectUri;
        this.clientId = clientId;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getRedirectUri() {
        return redirectUri;
    }

    @Override
    public String getClientId() {
        return clientId;
    }
}
