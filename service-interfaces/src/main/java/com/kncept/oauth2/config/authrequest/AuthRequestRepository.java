package com.kncept.oauth2.config.authrequest;

import java.util.Optional;

public interface AuthRequestRepository {

    // save a new session
    AuthRequest createAuthRequest(
        String oauthSessionId,
        Optional<String> state,
        Optional<String> nonce,
        String redirectUri,
        String clientId,
        String responseType
    );


    Optional<AuthRequest> lookup(String oauthSessionId);

}
