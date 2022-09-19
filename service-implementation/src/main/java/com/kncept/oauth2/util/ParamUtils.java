package com.kncept.oauth2.util;

import java.util.Map;
import java.util.Optional;

public class ParamUtils {

    public static String required(String name, Map<String, String> params) {
        String value = params.get(name);
        if (value == null) throw new RuntimeException("Required Param missing: " + name);
        value = value.trim();
        if (value.equals("")) throw new RuntimeException("Required Param is empty: " + name);
        return value;
    }
    public static String optional(String name, Map<String, String> params, String defaultValue) {
        String value = params.get(name);
        if (value == null) return defaultValue;
        value = value.trim();
        if (value.equals("")) return defaultValue;
        return value;
    }
    public static Optional<String> optional(String name, Map<String, String> params) {
        String value = params.get(name);
        if (value == null) return Optional.empty();
        value = value.trim();
        if (value.equals("")) return Optional.empty();
        return Optional.of(value);
    }

}
