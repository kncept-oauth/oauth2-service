package com.kncept.oauth2.config.authrequest;

import com.kncept.oauth2.config.annotation.OidcExpiryTime;
import com.kncept.oauth2.config.annotation.OidcId;

import java.util.Optional;

public interface AuthRequest {

    @OidcId String oauthSessionId();
    Optional<String> state();
    Optional<String> nonce();
    String redirectUri();
    String clientId();
    String responseType();

    @OidcExpiryTime long expiryTime();
}
