package com.kncept.oauth2.operation.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RenderedContentResponse implements OperationResponse {
    private final Map<String, String> headers = new HashMap<>();
    private final int responseCode;
    private final String content;
    private final Optional<String> oauthSessionId;

    public RenderedContentResponse(
        int responseCode,
        String content,
        String contentType,
        Optional<String> oauthSessionId
    ) {
        this.responseCode = responseCode;
        this.content = content;
        this.oauthSessionId = oauthSessionId;
        headers.put("Content-Type", contentType);
    }

    public RenderedContentResponse(ContentResponse source, String content) {
        this.responseCode = source.responseCode();
        this.content = content;
        this.oauthSessionId = source.oauthSessionId();
        this.headers.putAll(source.headers());
    }

    @Override
    public int responseCode() {
        return responseCode;
    }

    public RenderedContentResponse addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public String content() {
        return content;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Optional<String> oauthSessionId() {
        return oauthSessionId;
    }
}
