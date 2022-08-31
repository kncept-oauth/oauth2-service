package com.kncept.oauth2.session.repository;

import com.kncept.oauth2.session.OauthSession;

import java.util.Optional;

public interface OauthSessionRepository {

    Optional<OauthSession> lookupSession(String oauthSessionId);

    OauthSession createSession();

}
