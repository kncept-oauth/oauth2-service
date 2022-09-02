package com.kncept.oauth2.config.session;

import java.util.Optional;

public interface OauthSessionRepository {

    // creates a new UNAUTHENTICATED session
    OauthSession createSession();

    // looks up an (unexpired) session by id
    Optional<OauthSession> lookup(String oauthSessionId);

    // AUTHENTICATES a session with a userId
    Optional<OauthSession> authenticate(String oauthSessionId, String userId);

}
