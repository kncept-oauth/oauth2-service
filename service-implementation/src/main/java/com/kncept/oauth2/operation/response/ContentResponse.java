package com.kncept.oauth2.operation.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ContentResponse implements OperationResponse {
    public enum Content {
        LOGIN_PAGE,
        SIGNUP_PAGE,
        ERROR_PAGE,
        CSS
    }

    private final int responseCode;
    private final Content content;
    private final Optional<String> oauthSessionId;
    private final Map<String, String> params;
    private final Map<String, String> headers;

    // HEADERS

    public ContentResponse(int responseCode, String contentType, Content content, Optional<String> oauthSessionId) {
        this.responseCode = responseCode;
        this.content = content;
        this.oauthSessionId = oauthSessionId;
        params = new HashMap<>();
        headers = new HashMap<>();
        headers.put("Content-Type", contentType);
    }

    public ContentResponse withParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    @Override
    public int responseCode() {
        return responseCode;
    }

    public Content content() {
        return content;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Optional<String> oauthSessionId() {
        return oauthSessionId;
    }

    public Map<String, String> params() {
        return params;
    }
}
