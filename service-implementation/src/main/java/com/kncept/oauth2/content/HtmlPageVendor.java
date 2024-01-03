package com.kncept.oauth2.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HtmlPageVendor {
    SimpleHtmlInflater inflater = new SimpleHtmlInflater();
    String hostedUrl;

    public HtmlPageVendor(String hostedUrl) {
        this.hostedUrl = hostedUrl;
    }

    public Map<String, String> defaultParams() {
        Map<String, String> params =  new HashMap<>();
        params.put("root", hostedUrl);
        return params;
    }

    public String errorPage(String error) {
        Map<String, String> params = defaultParams();
        params.put("error", error);
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/error.html"), params);
    }

    public String loginPage(Optional<String> message) {
        Map<String, String> params = defaultParams();
        params.put("message", message.map(m -> "<div id=\"login-signup-message\">" + m + "</div>").orElse(null));
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/login.html"), params);
    }

    public String signupPage(Optional<String> message) {
        Map<String, String> params = defaultParams();
        params.put("message", message.map(m -> "<div id=\"login-signup-message\">" + m + "</div>").orElse(null));
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/signup.html"), params);
    }

    public String verifyPage(Optional<String> message) {
        Map<String, String> params = defaultParams();
        params.put("message", message.map(m -> "<div id=\"login-signup-message\">" + m + "</div>").orElse(null));
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/verify.html"), params);
    }

    public String profilePage(Map<String, String> params) {
        Map<String, String> defaultParams = defaultParams();
        for(String key: defaultParams.keySet()) if (!params.containsKey(key)) params.put(key, defaultParams.get(key));
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/profile.html"), params);
    }

    public String css() {
        Map<String, String> params = defaultParams();
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/style.css"), params);
    }


}
