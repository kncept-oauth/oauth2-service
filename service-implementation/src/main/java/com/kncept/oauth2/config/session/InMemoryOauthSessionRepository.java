package com.kncept.oauth2.config.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryOauthSessionRepository implements OauthSessionRepository {
    Map<String, SimpleOauthSession> sessions = new HashMap<>();

    @Override
    public Optional<OauthSession> lookup(String oauthSessionId) {
        return Optional.ofNullable(sessions.get(oauthSessionId));
    }

    @Override
    public OauthSession createSession() {
        SimpleOauthSession session = new SimpleOauthSession(
                UUID.randomUUID().toString(),
                Optional.empty(),
                -1
        );
        sessions.put(session.oauthSessionId(), session);
        return session;
    }

    @Override
    public Optional<OauthSession> authenticate(String oauthSessionId, String userId) {
        SimpleOauthSession session = new SimpleOauthSession(oauthSessionId, Optional.of(userId), -1);
        sessions.put(session.oauthSessionId(), session);
        return Optional.of(session);
    }

}
