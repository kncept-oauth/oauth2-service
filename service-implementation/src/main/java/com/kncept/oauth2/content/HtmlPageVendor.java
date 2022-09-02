package com.kncept.oauth2.content;

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
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/error.html"), params);
    }

    public String loginPage(Optional<String> message, boolean includeSignup) {
        Map<String, String> params = defaultParams();
        params.put("message", message.orElse(null));
        if (includeSignup) params.put("signup", "<a href=\"signup\">Signup</a>");
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/login.html"), params);
    }

    public String signupPage(Optional<String> message) {
        Map<String, String> params = defaultParams();
        params.put("message", message.orElse(null));
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/signup.html"), params);
    }

    public String css() {
        Map<String, String> params = defaultParams();
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/style.css"), params);
    }


}
