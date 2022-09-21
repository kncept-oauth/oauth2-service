package com.kncept.oauth2.crypto.key;

import com.kncept.oauth2.date.DateRange;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

// This part is subject to change..
// TODO: add nice import/export capability (eg: ship with pregenerated pem files)
public class KeyGenerator {

    // see https://www.novixys.com/blog/how-to-generate-rsa-keys-java/ for hints
    public KeyPair generateKeypair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // add a default 18 hour expiration
    public ExpiringKeyPair addExpiration(KeyPair keyPair) {
        LocalDateTime now = LocalDateTime.now();
        return new ExpiringKeyPair(
                keyPair,
                new DateRange(now, now.plusHours(18))
        );
    }



}
