package com.kncept.oauth2.crypto.key;

import com.kncept.oauth2.config.Oauth2StorageConfiguration;
import com.kncept.oauth2.crypto.key.strategy.KeypairStrategy;
import com.kncept.oauth2.crypto.key.strategy.PresharedKeypairStrategy;
import com.kncept.oauth2.crypto.key.strategy.RotatingKeypairStrategy;
import com.kncept.oauth2.crypto.key.strategy.StaticKeypairStrategy;
import com.kncept.oauth2.entity.EntityId;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

import static com.kncept.oauth2.config.EnvPropertyConfiguration.env;

public class KeyManager {

    public static final String OIDC_KEYS_ROOT_PROPERTY = "OIDC_Keys";

    static interface KeyManagerEnum {
        String name();
    }

    public enum Strategy implements KeyManagerEnum {
        Preshared,
        Static,
        Rotating,
        ;
    }

    private final Oauth2StorageConfiguration config;
    private final KeypairStrategy strategy;

//    private static String getEnvProperty(String suffix) {
//        return env(OIDC_KEYS_ROOT_PROPERTY + "_" + suffix);
//    }

    public KeyManager(Oauth2StorageConfiguration config) {
        this.config = config;
        this.strategy = strategy();
    }

    private KeypairStrategy strategy() {
        Strategy strategy = Strategy.Static;
        String strategyString = env(OIDC_KEYS_ROOT_PROPERTY);
        if (strategyString != null) strategy = Strategy.valueOf(strategyString);
        switch(strategy) {
            case Preshared: return new PresharedKeypairStrategy(OIDC_KEYS_ROOT_PROPERTY + "_" + strategy.name());
            case Static: return new StaticKeypairStrategy(config.expiringKeypairRepository());
            case Rotating: return new RotatingKeypairStrategy(config.expiringKeypairRepository());
        }
        throw new IllegalStateException();
    }

    public ManagedKeypair current() {
        return strategy.current();
    }




/*
eg:
{"jwk":
  [
    {"alg":"EC",
     "crv":"P-256",
     "x":"MKBCTNIcKUSDii11ySs3526iDZ8AiTo7Tu6KPAqv7D4",
     "y":"4Etl6SRW2YiLUrN5vfvVHuhp7x8PxltmWWlbbM4IFyM",
     "use":"enc",
     "kid":"1"},

    {"alg":"RSA",
     "mod": "0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx
4cbbfAAtVT86zwu1RK7aPFFxuhDR1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMs
tn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6Cf0h4QyQ5v-65YGjQR0_FDW2
QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1n91CbOpbI
SD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3XPksINHaQ-G_xBniIqb
w0Ls1jF44-csFCur-kEgU8awapJzKnqDKgw",
     "exp":"AQAB",
     "kid":"2011-04-29"}
  ]
}
 */

    // https://openid.net/specs/draft-jones-json-web-key-03.html#ExampleJWK
    public Map<String, Object> jwks() {
        ManagedKeypair keys = current();
        Map<String, Object> container = new HashMap<>();
        List<Map<String, Object>> array = new LinkedList<>();
        container.put("jwk", array);
        array.add(jwksFormat(keys.id(), keys.keyPair().getPublic()));
        return container;
    }

    public Map<String, Object> jwksFormat(EntityId keyId, PublicKey key) {
        Map<String, Object> jwksFormat = new HashMap<>();
        jwksFormat.put("kid", keyId.toString());
        jwksFormat.put("use", "sig"); // optional

        String alg = key.getAlgorithm();
        jwksFormat.put("alg", alg);

        if (alg.equals("EC")) {
            ec(jwksFormat, (ECPublicKey) key);
        } else if (alg.equals("RSA")) {
            rsa(jwksFormat, (RSAPublicKey) key);
        }

        return jwksFormat;
    }

    void ec(Map<String, Object> jwksFormat, ECPublicKey key) {
//        key.getFormat() X.509
//            key.getAlgorithm() EC
//        if (key.getParams() instanceof NamedCurve)

        // this is sooo hacky a way to get the curv
        String curve = "error";
        try {
            curve = key.getParams().toString().split(" ")[0];
        } catch (Exception e) {}

        jwksFormat.put("crv", curve);
        jwksFormat.put("x", b64(key.getW().getAffineX()));
        jwksFormat.put("y", b64(key.getW().getAffineY()));
    }

    void rsa(Map<String, Object> jwksFormat, RSAPublicKey key) {
        jwksFormat.put("mod", b64(key.getModulus()));
        jwksFormat.put("exp", b64(key.getPublicExponent()));
    }

    // custom = just drop the PKCS8 encoding. nice 'n easy :)
    public Map<String, Object> pkcs8() {
        ManagedKeypair keys = current();
        Map<String, Object> container = new HashMap<>();
        List<Map<String, Object>> array = new LinkedList<>();
        container.put("pkcs8", array);
        array.add(pkcs8Format(keys.id(), keys.keyPair().getPublic()));
        return container;
    }
    public Map<String, Object> pkcs8Format(EntityId keyId, PublicKey key) {
        Map<String, Object> pkcs8Format = new HashMap<>();
        pkcs8Format.put("kid", keyId.toString());
        pkcs8Format.put("value", new PKCS8KeypairParser().outputPublic(key));
        return pkcs8Format;
    }

    String b64(BigInteger bigInteger) {
        return Base64.getEncoder().withoutPadding().encodeToString(bigInteger.toByteArray());
    }
}
