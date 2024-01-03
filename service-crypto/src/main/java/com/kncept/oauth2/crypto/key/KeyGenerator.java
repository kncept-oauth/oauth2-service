package com.kncept.oauth2.crypto.key;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;

// This part is subject to change..
// TODO: add nice import/export capability (eg: ship with pregenerated pem files)
public class KeyGenerator {

    // see https://www.novixys.com/blog/how-to-generate-rsa-keys-java/ for hints
    public KeyPair generateKeypair() {
        return generateEcKeypair();
    }
    public KeyPair generateRsaKeypair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(4096);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public KeyPair generateEcKeypair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec spec = new ECGenParameterSpec("secp256r1");
            kpg.initialize(spec);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

}
