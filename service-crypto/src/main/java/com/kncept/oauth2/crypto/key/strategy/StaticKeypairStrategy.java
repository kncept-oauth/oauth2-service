package com.kncept.oauth2.crypto.key.strategy;

import com.kncept.oauth2.config.crypto.ExpiringKeypairRepository;
import com.kncept.oauth2.crypto.key.KeyGenerator;
import com.kncept.oauth2.crypto.key.ManagedKeypair;
import com.kncept.oauth2.crypto.key.ManagedKeypairConverter;
import com.kncept.oauth2.date.DateRange;

import java.security.KeyPair;
import java.util.UUID;

public class StaticKeypairStrategy implements KeypairStrategy {

    private final ExpiringKeypairRepository repository;

    private ManagedKeypair cached;

    public StaticKeypairStrategy(ExpiringKeypairRepository repository) {
        this.repository = repository;
    }

    @Override
    public ManagedKeypair current() {
        if (cached != null && cached.isValid()) return cached;
        cached = null;
        ManagedKeypair current = repository.latest().map(ManagedKeypairConverter::convert).orElse(null);
        if (current != null && current.isValid()) {
            cached = current;
            return cached;
        }
        KeyPair kp = new KeyGenerator().generateKeypair();
        cached = new ManagedKeypair(
                UUID.randomUUID().toString(),
                kp,
                DateRange.infinite
        );
        repository.save(ManagedKeypairConverter.convert(cached));
        return cached;
    }
}
