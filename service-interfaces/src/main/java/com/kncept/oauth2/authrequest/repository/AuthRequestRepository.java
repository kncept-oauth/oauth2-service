package com.kncept.oauth2.authrequest.repository;

import com.kncept.oauth2.authrequest.AuthRequest;

import java.util.Optional;

public interface AuthRequestRepository {

    // save a new session
    AuthRequest createAuthRequest(
        String oauthSessionId,
        String code, // authCode
        Optional<String> state,
        Optional<String> nonce,
        String redirectUri,
        String clientId,
        String responseType
    );

    Optional<AuthRequest> lookupByOauthSessionId(String oauthSessionId);

    Optional<AuthRequest> lookupByCode(String code);

}
