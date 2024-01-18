package com.kncept.oauth2.crypto.key;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.kncept.oauth2.crypto.util.StringUtils.splitToMultiLine;

public class PKCS8KeypairParser implements TextKeypairParser {

    private KeyFactory keyFactory;

    private static final String pkcs8PublicKeyHeader = "-----BEGIN PUBLIC KEY-----";
    private static final String pkcs8PublicKeyTrailer = "-----END PUBLIC KEY-----";

    private static final String pkcs8PrivateKeyHeader = "-----BEGIN PRIVATE KEY-----";
    private static final String pkcs8PrivateKeyTrailer = "-----END PRIVATE KEY-----";

    private static String nl = "\n";

    @Override
    public boolean publicIsParsable(String s) {
        return s.startsWith(pkcs8PublicKeyHeader);
    }

    @Override
    public boolean privateIsParsable(String s) {
        return s.startsWith(pkcs8PrivateKeyHeader);
    }

    private synchronized KeyFactory rsaKeyFactory() {
        if (keyFactory == null) try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return keyFactory;

    }

    @Override
    public PublicKey parsePublic(String s) {
        s = s
                .replaceAll(pkcs8PublicKeyHeader, "")
                .replaceAll("\\n", "")
                .replaceAll(pkcs8PublicKeyTrailer, "");

        KeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(s));
        try {
            return rsaKeyFactory().generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PrivateKey parsePrivate(String s) {
        s = s
                .replaceAll(pkcs8PrivateKeyHeader, "")
                .replaceAll("\\n", "")
                .replaceAll(pkcs8PrivateKeyTrailer, "");

        KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(s));
        try {
            return rsaKeyFactory().generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String outputPublic(PublicKey k) {
        return pkcs8PublicKeyHeader + nl
                // line length of 64 is required
                + splitToMultiLine(Base64.getEncoder().encodeToString(k.getEncoded()), 64)
                + nl + pkcs8PublicKeyTrailer;
    }

    @Override
    public String outputPrivate(PrivateKey k) {
        return pkcs8PrivateKeyHeader + nl
                // line length of 64 is required
                + splitToMultiLine(Base64.getEncoder().encodeToString(k.getEncoded()), 64)
                + nl + pkcs8PrivateKeyTrailer;
    }

}
