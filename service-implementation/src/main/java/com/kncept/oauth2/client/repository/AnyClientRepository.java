package com.kncept.oauth2.client.repository;

import com.kncept.oauth2.client.Client;

import java.util.Optional;

public class AnyClientRepository implements ClientRepository {

    @Override
    public Optional<Client> getClientById(String clientId) {
        return Optional.of(new AnyClient(clientId));
    }

    static class AnyClient implements Client {
        private final String clientId;

        public AnyClient(String clientId) {
            this.clientId = clientId;
        }

        @Override
        public String clientId() {
            return clientId;
        }
    }
}
