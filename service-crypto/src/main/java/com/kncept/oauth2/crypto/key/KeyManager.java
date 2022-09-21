package com.kncept.oauth2.crypto.key;

import com.kncept.oauth2.config.Oauth2Configuration;
import com.kncept.oauth2.crypto.key.strategy.KeypairStrategy;
import com.kncept.oauth2.crypto.key.strategy.PresharedKeypairStrategy;
import com.kncept.oauth2.crypto.key.strategy.RotatingKeypairStrategy;
import com.kncept.oauth2.crypto.key.strategy.StaticKeypairStrategy;

import static com.kncept.oauth2.config.Oauth2Configuration.env;

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

    private final Oauth2Configuration config;
    private final KeypairStrategy strategy;

//    private static String getEnvProperty(String suffix) {
//        return env(OIDC_KEYS_ROOT_PROPERTY + "_" + suffix);
//    }

    public KeyManager(Oauth2Configuration config) {
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

}
