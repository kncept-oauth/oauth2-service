package com.kncept.oauth2.config.crypto;

import java.util.List;
import java.util.Optional;

public interface ExpiringKeypairRepository {

    Optional<ExpiringKeypair> lookup(String id);

    Optional<ExpiringKeypair> latest();

    void save(ExpiringKeypair kp);

    // OPTIONAL - just return an empty list if you don't want to implement it
    List<ExpiringKeypair> all();

}
