package com.kncept.oauth2.crypto.key.strategy;

import com.kncept.oauth2.config.crypto.ExpiringKeypairRepository;
import com.kncept.oauth2.crypto.key.ManagedKeypair;

public class RotatingKeypairStrategy implements KeypairStrategy {

    private final ExpiringKeypairRepository repository;

    public RotatingKeypairStrategy(ExpiringKeypairRepository repository) {
        this.repository = repository;
    }

    @Override
    public ManagedKeypair current() {
        // TODO Support this Operation
        throw new UnsupportedOperationException();
    }
}
