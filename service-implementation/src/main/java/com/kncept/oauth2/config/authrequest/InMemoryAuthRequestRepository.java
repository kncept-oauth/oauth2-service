package com.kncept.oauth2.config.authrequest;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class InMemoryAuthRequestRepository implements AuthRequestRepository {
    private final Map<String, SimpleAuthRequest> activeRequests = new TreeMap<>();

    @Override
    public AuthRequest createAuthRequest(
            String oauthSessionId,
            Optional<String> state,
            Optional<String> nonce,
            String redirectUri,
            String clientId,
            String responseType
    ) {
        SimpleAuthRequest authRequest = new SimpleAuthRequest(
                oauthSessionId,
                state,
                nonce,
                redirectUri,
                clientId,
                responseType,
                0
        );
        activeRequests.put(oauthSessionId, authRequest);
        return authRequest;
    }

    @Override
    public Optional<AuthRequest> lookup(String oauthSessionId) {
        return Optional.ofNullable(activeRequests.get(oauthSessionId));
    }

}
