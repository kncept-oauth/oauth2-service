package com.kncept.oauth2.util;

import com.kncept.oauth2.operation.response.RenderedContentResponse;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Optional;

public class JsonUtils {

    public static String toJson(Map<String, ?> map) {
        JSONObject obj = new JSONObject();
        for(Map.Entry<String, ?> e: map.entrySet())
            obj.put(e.getKey(), e.getValue());
        return obj.toJSONString();
    }

    public static RenderedContentResponse jsonError(String error, Optional<String> oauthSessionId) {
        return jsonError(400, error, oauthSessionId);
    }

    public static RenderedContentResponse jsonError(int responseCode, String error, Optional<String> oauthSessionId) {
        return new RenderedContentResponse(
                responseCode,
                toJson(new MapBuilder<String, String>().with("error", error).get()),
                "application/json",
                oauthSessionId,
                false);
    }

}
