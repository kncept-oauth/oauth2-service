package com.kncept.oauth2.operation.response;

public class RedirectResponse implements OperationResponse {

    private final String redirectUri;

    public RedirectResponse(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Override
    public int responseCode() {
        return 302;
    }

    public String redirectUri() {
        return redirectUri;
    }
}
