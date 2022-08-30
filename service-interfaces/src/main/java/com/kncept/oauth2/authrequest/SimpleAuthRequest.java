package com.kncept.oauth2.authrequest;

public class SimpleAuthRequest implements AuthRequest {

    final String state; // nullable
    final String redirectUri;
    final String clientId;
    final String responseType;
    final String code;

    public SimpleAuthRequest(
            String state,
            String redirectUri,
            String clientId,
            String responseType,
            String code
    ){
        this.state = state;
        this.redirectUri = redirectUri;
        this.clientId = clientId;
        this.responseType = responseType;
        this.code = code;
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

    @Override
    public String getResponseType() {
        return responseType;
    }

    @Override
    public String getCode() {
        return code;
    }
}
