package com.kncept.oauth2.session.repository;

import com.kncept.oauth2.session.OauthSession;

import java.util.Optional;

public interface OauthSessionRepository {

    // creates a new UNAUTHENTICATED session
    OauthSession createSession();

    // looks up an (unexpired) session by id
    Optional<OauthSession> lookupSession(String oauthSessionId);

    // AUTHENTICATES a session with a userId
    Optional<OauthSession> authenticateSession(String oauthSessionId, String userId);

}
