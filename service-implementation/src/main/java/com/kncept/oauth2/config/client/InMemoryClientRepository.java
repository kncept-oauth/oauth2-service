package com.kncept.oauth2.config.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryClientRepository implements ClientRepository {

    private final Map<String, InMemoryClient> clients = new HashMap<>();

    @Override
    public Optional<Client> lookup(String clientId) {
        return Optional.of(clients.get(clientId));
    }

    public void createClient(String clientId) {
        if (!clients.containsKey(clientId)) clients.put(clientId, new InMemoryClient(clientId));
    }

    private static class InMemoryClient implements Client {
        private final String clientId;

        public InMemoryClient(String clientId) {
            this.clientId = clientId;
        }

        @Override
        public String clientId() {
            return clientId;
        }
    }
}
