package com.kncept.oauth2.crypto.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthCryptoTest {

    AuthCrypto crypto = new AuthCrypto();

    @Test
    public void defaultAlgorithm() {
        assertEquals("b64(sha256)", crypto.hasher().algorithm());
    }

    // HEX HASHES obtained externally by http://www.sha1-online.com/
    // This is not an endorsement, simply a record of provenance
    @Test
    public void canMD5() {
        PasswordHasher hasher = crypto.hasher("hex(md5)");
        String hash = hasher.hash("test", "test");
        assertEquals("05a671c66aefea124cc08b76ea6d30bb", hash);
    }

    @Test
    public void canSha1() {
        PasswordHasher hasher = crypto.hasher("hex(sha1)");
        String hash = hasher.hash("test", "test");
        assertEquals("51abb9636078defbf888d8457a7c76f85c8f114c", hash);
    }

    @Test
    public void canSha256() {
        PasswordHasher hasher = crypto.hasher("hex(sha256)");
        String hash = hasher.hash("test", "test");
        assertEquals("37268335dd6931045bdcdf92623ff819a64244b53d0e746d438797349d4da578", hash);
    }

}
