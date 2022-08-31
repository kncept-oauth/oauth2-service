package com.kncept.oauth2.operation.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ContentResponse implements OperationResponse {
    public enum ContentType {
        LOGIN_PAGE,
        SIGNUP_PAGE,
        ERROR_PAGE,
        CSS
    }

    private final int responseCode;
    private final ContentType type;
    private final Optional<String> oauthSessionId;
    private final Map<String, String> params;

    public ContentResponse(int responseCode, ContentType type, Optional<String> oauthSessionId) {
        this.responseCode = responseCode;
        this.type = type;
        this.oauthSessionId = oauthSessionId;
        params = new HashMap<>();
    }

    public ContentResponse withParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    @Override
    public int responseCode() {
        return responseCode;
    }

    public ContentType type() {
        return type;
    }

    public Optional<String> oauthSessionId() {
        return oauthSessionId;
    }

    public Map<String, String> params() {
        return params;
    }
}
