package com.kncept.oauth2.crypto.key;

import com.kncept.oauth2.config.crypto.ExpiringKeypair;
import com.kncept.oauth2.date.DateRange;

import java.security.KeyPair;

public class ManagedKeypairConverter {

    public static ManagedKeypair convert(ExpiringKeypair key) {
        TextKeypairParser parser = new PKCS8KeypairParser();
        return new ManagedKeypair(
                key.getId(),
                new KeyPair(
                        parser.parsePublic(key.getPublicKey()),
                        parser.parsePrivate(key.getPublicKey())
                ),
                new DateRange(
                        key.getValidFrom(),
                        key.getValidTo()
                )
        );
    }

    public static ExpiringKeypair convert(ManagedKeypair key) {
        TextKeypairParser parser = new PKCS8KeypairParser();
        ExpiringKeypair keypair = new ExpiringKeypair();
        keypair.setId(key.id());
        keypair.setPrivateKey(parser.outputPrivate(key.keyPair().getPrivate()));
        keypair.setPublicKey(parser.outputPublic(key.keyPair().getPublic()));
        keypair.setValidFrom(key.validity().start());
        keypair.setValidTo(key.validity().end());
        keypair.setExpiry(key.validity().end());
        return keypair;
    }
}
