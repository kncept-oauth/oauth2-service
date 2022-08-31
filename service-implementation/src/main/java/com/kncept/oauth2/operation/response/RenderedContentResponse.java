package com.kncept.oauth2.operation.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RenderedContentResponse implements OperationResponse {
    public final Map<String, String> additionalHeaders = new HashMap<>();
    private final int responseCode;
    private final String content;
    private final Optional<String> oauthSessionId;

    public RenderedContentResponse(
        int responseCode,
        String content,
        Optional<String> oauthSessionId
    ) {
        this.responseCode = responseCode;
        this.content = content;
        this.oauthSessionId = oauthSessionId;
    }

    @Override
    public int responseCode() {
        return responseCode;
    }

    public RenderedContentResponse addHeader(String key, String value) {
        additionalHeaders.put(key, value);
        return this;
    }

    public String content() {
        return content;
    }

    public Optional<String> oauthSessionId() {
        return oauthSessionId;
    }
}
