package com.kncept.oauth2.crypto;

import com.kncept.oauth2.date.DateRange;

import java.security.KeyPair;
import java.time.LocalDateTime;

public class ExpiringKeyPair {
    private final KeyPair keyPair;
    private final DateRange validity;

    public ExpiringKeyPair(
            KeyPair keyPair,
            DateRange validity
    ) {
        this.keyPair = keyPair;
        this.validity = validity;
    }

    public DateRange validity() {
        return validity;
    }

    public KeyPair keyPair() {
        return keyPair;
    }

    public boolean isValid() {
        return validity.contains(LocalDateTime.now());
    }

}
