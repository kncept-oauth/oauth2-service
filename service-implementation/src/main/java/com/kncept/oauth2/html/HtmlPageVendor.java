package com.kncept.oauth2.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HtmlPageVendor {
    SimpleHtmlInflater inflater = new SimpleHtmlInflater();

    public Map<String, String> defaultParams() {
        return new HashMap<>();
    }

    public String errorPage(String error) {
        Map<String, String> params = defaultParams();
        params.put("error", error);
        return inflater.inflate(inflater.loadHtmlPageResource("/html/error.html"), params);
    }

    public String loginPage(Optional<String> message, boolean includeSignup) {
        Map<String, String> params = defaultParams();
        params.put("message", message.orElse(null));
        if (includeSignup) params.put("signup", "<a href=\"/signup\">Signup</a>");
        return inflater.inflate(inflater.loadHtmlPageResource("/html/login.html"), params);
    }

    public String signupPage(Optional<String> message) {
        Map<String, String> params = defaultParams();
        params.put("message", message.orElse(null));
        return inflater.inflate(inflater.loadHtmlPageResource("/html/signup.html"), params);
    }

    public String css() {
        Map<String, String> params = defaultParams();
        return inflater.inflate(inflater.loadHtmlPageResource("/style/style.css"), params);
    }


}
