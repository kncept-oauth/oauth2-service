package com.kncept.oauth2.crypto.key;

import com.kncept.oauth2.date.DateRange;

import java.security.KeyPair;
import java.time.LocalDateTime;

public class ManagedKeypair {
    private final String id;
    private final KeyPair keyPair;
    private final DateRange validity;

    public ManagedKeypair(
            String id,
            KeyPair keyPair,
            DateRange validity
    ) {
        this.id = id;
        this.keyPair = keyPair;
        this.validity = validity;
    }

    public DateRange validity() {
        return validity;
    }

    public String id() {
        return id;
    }

    public KeyPair keyPair() {
        return keyPair;
    }

    public boolean isValid() {
        return validity == null || validity.contains(LocalDateTime.now());
    }

}
