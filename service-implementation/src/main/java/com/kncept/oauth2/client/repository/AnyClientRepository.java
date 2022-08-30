package com.kncept.oauth2.client.repository;

import com.kncept.oauth2.client.Client;

public class AnyClientRepository implements ClientRepository {

    @Override
    public Client getClientById(String clientId) {
        return new AnyClient(clientId);
    }

    static class AnyClient implements Client {
        private final String clientId;

        public AnyClient(String clientId) {
            this.clientId = clientId;
        }

        @Override
        public String getClientId() {
            return clientId;
        }
    }
}
