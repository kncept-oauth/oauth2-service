package com.kncept.oauth2.crypto.key;

import com.kncept.oauth2.crypto.key.ExpiringKeyPair;
import com.kncept.oauth2.crypto.key.KeyGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public class KeyGeneratorTest {

    @Test
    public void canGenerateKeypair() {
        KeyGenerator keygen = new KeyGenerator();
        KeyPair kp = keygen.generateKeypair();
        Assertions.assertNotNull(kp.getPublic());
        Assertions.assertNotNull(kp.getPrivate());
    }

    @Test
    public void defaultExpiryWindowIs18h() {
        KeyGenerator keygen = new KeyGenerator();
        ExpiringKeyPair kp = keygen.addExpiration(keygen.generateKeypair());
        long notBeforeTime = kp.validity().start().toInstant(ZoneOffset.UTC).toEpochMilli();
        long expiryTime = kp.validity().end().toInstant(ZoneOffset.UTC).toEpochMilli();

        // check within 10ms... just in case of something funny happening
        long difference = expiryTime - notBeforeTime;
        difference -= TimeUnit.HOURS.toMillis(18);
        difference = Math.abs(difference);
        Assertions.assertTrue(difference < 10);
    }
}
