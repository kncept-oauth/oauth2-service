package com.kncept.oauth2.authrequest.repository;

import com.kncept.oauth2.authrequest.AuthRequest;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class InMemoryAuthRequestRepository implements AuthRequestRepository {

    private final Map<String, AuthRequest> activeRequests = new TreeMap<>();


    @Override
    public String createAuthRequest(AuthRequest newSession) {
        String sessionId = UUID.randomUUID().toString();
        activeRequests.put(sessionId, newSession);
        return sessionId;
    }

    @Override
    public AuthRequest lookupByOauthSessionId(String oauthSessionId) {
        return activeRequests.get(oauthSessionId);
    }
}
