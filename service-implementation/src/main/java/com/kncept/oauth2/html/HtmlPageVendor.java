package com.kncept.oauth2.html;

import java.util.HashMap;
import java.util.Map;

public class HtmlPageVendor {
    SimpleHtmlInflater inflater = new SimpleHtmlInflater();

    public String errorPage(String error) {
        Map<String, String> params = new HashMap<>();
        params.put("error", error);
        return inflater.inflate(inflater.loadHtmlPageResource("/html/error.html"), params);
    }

    public String loginPage(String oauthSessionId, String message, boolean includeSignup) {
        Map<String, String> params = new HashMap<>();
        params.put("oauthSessionId", oauthSessionId);
        params.put("message", message);
        if (includeSignup) params.put("signup", "<a href=\"/signup\">Signup</a>");
        return inflater.inflate(inflater.loadHtmlPageResource("/html/login.html"), params);
    }

}
