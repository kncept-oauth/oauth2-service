package com.kncept.oauth2.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HtmlPageVendor {
    SimpleHtmlInflater inflater = new SimpleHtmlInflater();

    public Map<String, String> defaultParams() {
        // TODO: add 'root', IF KNOWN
        return new HashMap<>();
    }

    public String errorPage(String error) {
        Map<String, String> params = defaultParams();
        params.put("error", error);
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/error.html"), params);
    }

    public String loginPage(Optional<String> message, boolean signupEnabled) {
        return loginSignup(message, signupEnabled, true);
    }

    public String signupPage(Optional<String> message) {
        return loginSignup(message, true, false);
    }

    private String loginSignup(Optional<String> message, boolean signupEnabled, boolean isLoginPage) {
        if (!signupEnabled && !isLoginPage) return errorPage("Signup is not enabled");

        Map<String, String> params = defaultParams();
        params.put("message", message.map(m -> "<div id=\"login-signup-message\">" + m + "</div>").orElse(null));
        params.put("action", isLoginPage ? "login" : "signup");
        params.put("buttonText", isLoginPage ? "Login" : "Signup");
        params.put("title", isLoginPage ? "Oauth Login" : "Account Signup");
        if (isLoginPage && signupEnabled) params.put("trailer", "<span id=\"login-signup-trailer\">Or <a href=\"signup\">Signup</a></span>");
        if (!isLoginPage) params.put("trailer", "<span id=\"login-signup-trailer\">Back to <a href=\"login\">Login</a></span>");
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/loginsignup.html"), params);
    }

    public String css() {
        Map<String, String> params = defaultParams();
        return inflater.inflate(inflater.loadHtmlPageResource("/simple-response-content/style.css"), params);
    }


}
