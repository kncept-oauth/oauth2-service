package com.kncept.oauth2.crypto.key;

import com.kncept.oauth2.config.crypto.ExpiringKeypair;
import com.kncept.oauth2.config.crypto.SimpleExpiringKeypair;
import com.kncept.oauth2.date.DateRange;

import java.security.KeyPair;
import java.time.ZoneOffset;

public class ManagedKeypairConverter {

    public static ManagedKeypair convert(ExpiringKeypair key) {
        TextKeypairParser parser = new PKCS8KeypairParser();
        return new ManagedKeypair(
                key.id(),
                new KeyPair(
                        parser.parsePublic(key.publicKey()),
                        parser.parsePrivate(key.publicKey())
                ),
                new DateRange(
                        key.validFrom(),
                        key.validTo()
                )
        );
    }

    public static ExpiringKeypair convert(ManagedKeypair key) {
        TextKeypairParser parser = new PKCS8KeypairParser();
        return new SimpleExpiringKeypair(
                key.id(),
                parser.outputPrivate(key.keyPair().getPrivate()),
                parser.outputPublic(key.keyPair().getPublic()),
                key.validity().start().toEpochSecond(ZoneOffset.UTC),
                key.validity().end().toEpochSecond(ZoneOffset.UTC),
                key.validity().end().toEpochSecond(ZoneOffset.UTC)
        );
    }
}
