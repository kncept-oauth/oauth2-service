package com.kncept.oauth2.client.repository;

import com.kncept.oauth2.client.Client;

import java.util.Optional;

// i.e. all the applications (by name/clientId) that have access
public interface ClientRepository {

    Optional<Client> getClientById(String clientId);
}

