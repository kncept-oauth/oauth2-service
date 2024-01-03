package com.kncept.oauth2.crypto.key.strategy;

import com.kncept.oauth2.config.SimpleCrudRepository;
import com.kncept.oauth2.config.crypto.ExpiringKeypair;
import com.kncept.oauth2.crypto.key.ManagedKeypair;

public class RotatingKeypairStrategy implements KeypairStrategy {

    private final SimpleCrudRepository<ExpiringKeypair> repository;

    public RotatingKeypairStrategy(SimpleCrudRepository<ExpiringKeypair> repository) {
        this.repository = repository;
    }

    @Override
    public ManagedKeypair current() {
        // TODO Support this Operation
        throw new UnsupportedOperationException();
    }
}
