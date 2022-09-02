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
                responseType
        );
        activeRequests.put(oauthSessionId, authRequest);
        return authRequest;
    }

    @Override
    public Optional<AuthRequest> lookup(String oauthSessionId) {
        return Optional.ofNullable(activeRequests.get(oauthSessionId));
    }

    private static class SimpleAuthRequest implements AuthRequest {
        private final String oauthSessionId;
        private final Optional<String> state;
        private final Optional<String> nonce;
        private final String redirectUri;
        private final String clientId;
        private final String responseType;

        public SimpleAuthRequest(
                String oauthSessionId,
                Optional<String> state,
                Optional<String> nonce,
                String redirectUri,
                String clientId,
                String responseType
        ) {
            this.oauthSessionId = oauthSessionId;
            this.state = state;
            this.nonce = nonce;
            this.redirectUri = redirectUri;
            this.clientId = clientId;
            this.responseType = responseType;
        }

        @Override
        public Optional<String> state() {
            return state;
        }

        @Override
        public String redirectUri() {
            return redirectUri;
        }

        @Override
        public String clientId() {
            return clientId;
        }

        @Override
        public String responseType() {
            return responseType;
        }

        @Override
        public String oauthSessionId() {
            return oauthSessionId;
        }

        @Override
        public Optional<String> nonce() {
            return nonce;
        }
    }
}
