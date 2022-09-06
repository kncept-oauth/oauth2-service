package com.kncept.oauth2.config.client;

import java.util.Optional;

// i.e. all the applications (by name/clientId) that have access
public interface ClientRepository {

    Optional<Client> lookup(String clientId);

    Optional<Client> update(Client client);
}

