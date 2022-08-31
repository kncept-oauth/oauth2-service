package com.kncept.oauth2.session.repository;

import com.kncept.oauth2.session.OauthSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryOauthSessionRepository implements OauthSessionRepository {

    Map<String, SimpleOauthSession> sessions = new HashMap<>();

    @Override
    public Optional<OauthSession> lookupSession(String oauthSessionId) {
        return Optional.ofNullable(sessions.get(oauthSessionId));
    }

    @Override
    public OauthSession createSession() {
        SimpleOauthSession session = new SimpleOauthSession(UUID.randomUUID().toString());
        sessions.put(session.oauthSessionId(), session);
        return session;
    }

    private static class SimpleOauthSession implements OauthSession {
        private final String oauthSessionId;

        public SimpleOauthSession(
                String oauthSessionId
        ) {
           this.oauthSessionId = oauthSessionId;
        }

        @Override
        public String oauthSessionId() {
            return oauthSessionId;
        }
    }
}
