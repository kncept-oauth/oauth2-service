package com.kncept.oauth2.operation.response;

import java.util.HashMap;
import java.util.Map;

public class JsonResponse {
    public final Map<String, String> additionalHeaders = new HashMap<>();
    public final int resopnseCode;
    public final String json;

    public JsonResponse(
        int resopnseCode,
        String json
    ) {
        this.resopnseCode = resopnseCode;
        this.json = json;
    }

    public JsonResponse addHeader(String key, String value) {
        additionalHeaders.put(key, value);
        return this;
    }
}
