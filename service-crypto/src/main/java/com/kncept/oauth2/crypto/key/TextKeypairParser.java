package com.kncept.oauth2.crypto.key;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public interface TextKeypairParser {

    public boolean publicIsParsable(String s);
    public boolean privateIsParsable(String s);

    public PublicKey parsePublic(String s);
    public PrivateKey parsePrivate(String s);

    String outputPublic(PublicKey k);
    String outputPrivate(PrivateKey k);

    // without bringing in an external library, encryption options are limited
    // eg: BouncyCastle would allow a PKCS1 (ssh) parser to be added, at the very least
    default List<TextKeypairParser> parsers() {
        return List.of(
                new PKCS8KeypairParser()
        );
    }


}
