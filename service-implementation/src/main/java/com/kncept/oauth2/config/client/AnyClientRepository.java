package com.kncept.oauth2.config.client;

import java.util.Optional;

public class AnyClientRepository implements ClientRepository {

    @Override
    public Optional<Client> lookup(String clientId) {
        return Optional.of(new SimpleClient(clientId, true));
    }

    @Override
    public Optional<Client> update(Client client) {
        return Optional.of(client);
    }
}
