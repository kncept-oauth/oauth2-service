package com.kncept.oauth2.authrequest.repository;

import com.kncept.oauth2.authrequest.AuthRequest;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class InMemoryAuthRequestRepository implements AuthRequestRepository {
    private final Map<String, SimpleAuthRequest> activeRequests = new TreeMap<>();

    @Override
    public AuthRequest createAuthRequest(
            String oauthSessionId,
            String code,
            Optional<String> state,
            Optional<String> nonce,
            String redirectUri,
            String clientId,
            String responseType
    ) {
        SimpleAuthRequest authRequest = new SimpleAuthRequest(
                oauthSessionId,
                code,
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
    public Optional<AuthRequest> lookupByOauthSessionId(String oauthSessionId) {
        return Optional.ofNullable(activeRequests.get(oauthSessionId));
    }

    @Override
    public Optional<AuthRequest> lookupByCode(String code) {
        return (Optional) activeRequests.values().stream()
                .filter(r -> r.code().equals(code))
                .findAny();
    }

    private static class SimpleAuthRequest implements AuthRequest {
        private final String oauthSessionId;
        private final String code;
        private final Optional<String> state;
        private final Optional<String> nonce;
        private final String redirectUri;
        private final String clientId;
        private final String responseType;

        public SimpleAuthRequest(
                String oauthSessionId,
                String code,
                Optional<String> state,
                Optional<String> nonce,
                String redirectUri,
                String clientId,
                String responseType
        ) {
            this.oauthSessionId = oauthSessionId;
            this.code = code;
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
        public String code() {
            return code;
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
