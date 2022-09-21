package com.kncept.oauth2.crypto.key;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class KeyParserTestBase {

    abstract String publicKeyFileName();
    abstract String privateKeyFileName();

    abstract TextKeypairParser parser();

    String readKeyToString(String keyFileName) {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("/testkeys/" + keyFileName);
            return new String(in.readAllBytes()).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void publicKeyIsParsable() {
        assertTrue(parser().publicIsParsable(readKeyToString(publicKeyFileName())));
    }

    @Test
    public void privateKeyIsParseable() {
        assertTrue(parser().privateIsParsable(readKeyToString(privateKeyFileName())));
    }


    @Test
    public void canReadPemPublicKey() {
        String key = readKeyToString(publicKeyFileName());
        PublicKey pk = parser().parsePublic(key);
        String outPk = parser().outputPublic(pk);
        assertEquals(key, outPk);
    }

    @Test
    public void canReadPemPrivateKey() {
        String key = readKeyToString(privateKeyFileName());
        PrivateKey pk = parser().parsePrivate(key);
        String outPk = parser().outputPrivate(pk);
        assertEquals(key, outPk);
    }

}
