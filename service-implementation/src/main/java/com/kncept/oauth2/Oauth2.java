package com.kncept.oauth2;

import com.kncept.oauth2.authrequest.SimpleAuthRequest;
import com.kncept.oauth2.client.Client;
import com.kncept.oauth2.configuration.Oauth2Configuration;
import com.kncept.oauth2.operation.response.OperationResponse;

import java.util.Map;

public class Oauth2 {

    private Oauth2Configuration config = new Oauth2Configuration();

    public Oauth2() {
    }

    // https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
    public OperationResponse authorize(Map<String, String> params) {
        String scope = required("scope", params); // meant to have 'openid' in it for OIDC
        String responseType = required("response_type", params);
        String clientId = required("client_id", params);
        String redirectUri = required("redirect_uri", params);

        String state = optional("state", params, null);
//        String nonce = optional("nonce", params, null);


        boolean isPkce = config.requirePkce() || params.containsKey("code_challenge");
        if (config.requirePkce()) {
            String codeChallenge = "";//required("code_challenge", params);
            String codeChallengeMethod = "";//optional("code_challenge_method", params, "");
        }

        Client client = config.clientRepository().getClientById(clientId);
        if (client == null) {
            return new OperationResponse(
                    "text/html",
                    500,
                    config.htmlPageVendor().errorPage(
                            "Unknown Client ID: " + clientId
                    ));
        }

        String oauthSessionId = config.authRequestRepository().createAuthRequest(
                new SimpleAuthRequest(state, redirectUri, clientId));

        return new OperationResponse(
                "text/html",
                200,
                config.htmlPageVendor().loginPage(
                        oauthSessionId,
                        null,
                        config.userRepository().isAcceptingSignup()
                ));
    }

    public OperationResponse login(Map<String, String> params) {
        String oauthSessionId = params.get("oauthSessionId");
        if (oauthSessionId == null) {
            return new OperationResponse(
                    "text/html",
                    500, config.htmlPageVendor().errorPage(
                            "No oauthSessionId"
            ));
        }
        if (params.isEmpty() || !params.containsKey("password")) // just display login page with no attempts at anything else
            return new OperationResponse(
                    "text/html",
                    200,
                    config.htmlPageVendor().loginPage(
                            oauthSessionId,
                            null,
                            config.userRepository().isAcceptingSignup()
                    ));


        // its a login attempt
        String password = params.get("password");
        String username = params.get("username");

        boolean isAuthorized = config.userRepository().verifyUser(password, username);

        if (isAuthorized) {
            // redirect back to app.
            //
            // Potential option - use an interposing screen.
            // Use case - ignoring redirect URI and using this service
            // as an 'index' service
            // eg: these services have been authorized
            //   - app1
            //   - app2

            throw new RuntimeException("Successful auth - not yet implemented");

        } else {
            return new OperationResponse(
                    "text/html",
                    200,
                    config.htmlPageVendor().loginPage(
                            oauthSessionId,
                            "Authorization Failed - Please try again",
                            config.userRepository().isAcceptingSignup()
                    ));
        }
    }






    private String required(String name, Map<String, String> params) {
        String value = params.get(name);
        if (value == null) throw new RuntimeException("Required Param missing: " + name);
        value = value.trim();
        if (value.equals("")) throw new RuntimeException("Required Param is empty: " + name);
        return value;
    }

    private String optional(String name, Map<String, String> params, String defaultValue) {
        String value = params.get(name);
        if (value == null) return defaultValue;
        value = value.trim();
        if (value.equals("")) return defaultValue;
        return value;
    }



}
