package com.kncept.oauth2.authrequest;

import java.util.Optional;

public interface AuthRequest {

    Optional<String> state();
    Optional<String> nonce();
    String redirectUri();
    String clientId();
    String responseType();
    String code();
}
