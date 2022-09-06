package com.kncept.oauth2.config.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryClientRepository implements ClientRepository {

    private final Map<String, SimpleClient> clients = new HashMap<>();

    @Override
    public Optional<Client> lookup(String clientId) {
        return Optional.ofNullable(clients.get(clientId));
    }

    @Override
    public Optional<Client> update(Client client) {
        clients.put(client.clientId(), new SimpleClient(client.clientId(), client.enabled()));
        return Optional.of(client);
    }

    public void createClient(String clientId) {
        if (!clients.containsKey(clientId)) clients.put(clientId, new SimpleClient(clientId));
    }

}
