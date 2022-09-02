package com.kncept.oauth2.authrequest;

import java.util.Optional;

public interface AuthRequest {

    String oauthSessionId();
    Optional<String> state();
    Optional<String> nonce();
    String redirectUri();
    String clientId();
    String responseType();
}