package com.kncept.oauth2.crypto.key;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PKCS8KeypairParserTest extends KeyParserTestBase {
    private static final String privateKeyFile = "pkcs8private_key.pem";
    private static final String publicKeyFile = "pkcs8public_key.pem";

    @Override
    String publicKeyFileName() {
        return publicKeyFile;
    }

    @Override
    String privateKeyFileName() {
        return privateKeyFile;
    }

    @Override
    TextKeypairParser parser() {
        return new PKCS8KeypairParser();
    }


    @Test
    public void privateKeysContainNewlines() {
        assertEquals(52, readKeyToString(privateKeyFileName()).split("\n").length);
    }

    @Test
    public void publicKeysContainNewlines() {
        assertEquals(14, readKeyToString(publicKeyFileName()).split("\n").length);
    }

}