package com.kncept.oauth2.operation.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RenderedContentResponse implements OperationResponse {
    private final Map<String, String> headers = new HashMap<>();
    private final int responseCode;
    private final String content;
    private final Optional<String> oauthSessionId;
    private final boolean base64Encoded;

    public RenderedContentResponse(
        int responseCode,
        String content,
        String contentType,
        Optional<String> oauthSessionId,
        boolean base64Encoded
    ) {
        this.responseCode = responseCode;
        this.content = content;
        this.oauthSessionId = oauthSessionId;
        headers.put("Content-Type", contentType);
        this.base64Encoded = base64Encoded;
    }

    public RenderedContentResponse(ContentResponse source, String content, String contentType, boolean base64Encoded) {
        this.responseCode = source.responseCode();
        this.content = content;
        this.oauthSessionId = source.oauthSessionId();
        headers.put("Content-Type", contentType);
        this.base64Encoded = base64Encoded;
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

    public boolean base64Encoded() {
        return base64Encoded;
    }
}
