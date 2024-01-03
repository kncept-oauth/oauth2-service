package com.kncept.oauth2.crypto.key;

import com.kncept.oauth2.date.DateRange;
import com.kncept.oauth2.entity.EntityId;

import java.security.KeyPair;
import java.time.Clock;
import java.time.LocalDateTime;

public class ManagedKeypair {
    private final EntityId id;
    private final KeyPair keyPair;
    private final DateRange validity;

    public ManagedKeypair(
            EntityId id,
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

    public EntityId id() {
        return id;
    }

    public KeyPair keyPair() {
        return keyPair;
    }

    public boolean isValid() {
        return validity == null || validity.contains(LocalDateTime.now(Clock.systemUTC()));
    }

}
