package com.kncept.oauth2.crypto.key;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

public class KeyGeneratorTest {

    @Test
    public void canGenerateKeypair() {
        KeyGenerator keygen = new KeyGenerator();
        KeyPair kp = keygen.generateKeypair();
        Assertions.assertNotNull(kp.getPublic());
        Assertions.assertNotNull(kp.getPrivate());
    }

}
