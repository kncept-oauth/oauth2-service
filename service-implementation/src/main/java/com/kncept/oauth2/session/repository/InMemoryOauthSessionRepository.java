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
        SimpleOauthSession session = new SimpleOauthSession(UUID.randomUUID().toString(), Optional.empty());
        sessions.put(session.oauthSessionId(), session);
        return session;
    }

    @Override
    public Optional<OauthSession> authenticateSession(String oauthSessionId, String userId) {
        SimpleOauthSession session = new SimpleOauthSession(oauthSessionId, Optional.of(userId));
        sessions.put(session.oauthSessionId(), session);
        return Optional.of(session);
    }

    private static class SimpleOauthSession implements OauthSession {
        private final String oauthSessionId;
        private final Optional<String> userId;

        public SimpleOauthSession(
                String oauthSessionId,
                Optional<String> userId
        ) {
           this.oauthSessionId = oauthSessionId;
           this.userId = userId;
        }

        @Override
        public String oauthSessionId() {
            return oauthSessionId;
        }

        @Override
        public boolean authenticated() {
            return userId.isPresent();
        }

        @Override
        public Optional<String> userId() {
            return userId;
        }
    }
}
