package com.kncept.oauth2.config.authrequest;

import com.kncept.oauth2.config.annotation.OidcIdField;

import java.util.Optional;

public interface AuthRequest {

    @OidcIdField String oauthSessionId();
    Optional<String> state();
    Optional<String> nonce();
    String redirectUri();
    String clientId();
    String responseType();
}
