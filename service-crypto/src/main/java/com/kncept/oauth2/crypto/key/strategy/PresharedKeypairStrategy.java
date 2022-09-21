package com.kncept.oauth2.crypto.key.strategy;

import com.kncept.oauth2.crypto.key.ManagedKeypair;
import com.kncept.oauth2.crypto.key.PKCS8KeypairParser;
import com.kncept.oauth2.crypto.key.TextKeypairParser;
import com.kncept.oauth2.date.DateRange;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static com.kncept.oauth2.config.Oauth2Configuration.env;

public class PresharedKeypairStrategy implements KeypairStrategy {
    private final String propertyPrefix;

    private final ManagedKeypair key;
    public PresharedKeypairStrategy(String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;

        TextKeypairParser keypairParser = new PKCS8KeypairParser();

        PublicKey publicKey = keypairParser.parsePublic(env(propertyPrefix + "_Public"));
        PrivateKey privateKey = keypairParser.parsePrivate(env(propertyPrefix + "_Private"));

        key = new ManagedKeypair(
                "static",
                new KeyPair(publicKey, privateKey),
                DateRange.infinite
        );
    }

    public ManagedKeypair current() {
        return key;
    }

}
