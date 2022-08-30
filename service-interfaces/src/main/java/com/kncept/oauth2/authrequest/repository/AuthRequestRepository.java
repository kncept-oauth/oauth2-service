package com.kncept.oauth2.authrequest.repository;

import com.kncept.oauth2.authrequest.AuthRequest;

public interface AuthRequestRepository {

    // save a new session
    String createAuthRequest(AuthRequest newSession);

    // null if session not found
    AuthRequest lookupByOauthSessionId(String oauthSessionId);

}
