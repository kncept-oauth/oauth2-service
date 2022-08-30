package com.kncept.oauth2.client.repository;

import com.kncept.oauth2.client.Client;

// i.e. all the applications (by name/clientId) that have access
public interface ClientRepository {

    // this can return null
    Client getClientById(String clientId);
}

