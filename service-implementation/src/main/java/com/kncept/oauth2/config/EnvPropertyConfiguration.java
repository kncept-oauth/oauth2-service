package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authcode.AuthcodeRepository;
import com.kncept.oauth2.config.authrequest.AuthRequestRepository;
import com.kncept.oauth2.config.client.ClientRepository;
import com.kncept.oauth2.config.crypto.ExpiringKeypairRepository;
import com.kncept.oauth2.config.parameter.ParameterRepository;
import com.kncept.oauth2.config.session.OauthSessionRepository;
import com.kncept.oauth2.config.user.UserRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static com.kncept.oauth2.config.Oauth2Configuration.env;

public class EnvPropertyConfiguration implements Oauth2Configuration {

    private ConcurrentHashMap<Class, Object> repositories = new ConcurrentHashMap<>();

    public <T> T loadFromEnvProperty(Class<T> interfaceType) {
        return loadFromEnvProperty(interfaceType, null);
    }
    public <T> T loadFromEnvProperty(Class<T> interfaceType, Supplier<T> defaultValue) {
        return (T)repositories.computeIfAbsent(interfaceType, key -> {
            String propertySuffix = propertySuffixFromInterface(key);
            return loadClassFromEnvProperty(propertySuffix, defaultValue);
        });
    }

    String propertySuffixFromInterface(Class iface) {
        String name = iface.getSimpleName();
        if (name.endsWith("Repository")) {
            name = name.substring(0, name.length() - 10);
        } else throw new IllegalStateException("No strategy to obtain short property name from " + name);

        return name;
    }

    @Override
    public ClientRepository clientRepository() {
        return loadFromEnvProperty(ClientRepository.class);
    }

    @Override
    public synchronized AuthRequestRepository authRequestRepository() {
        return loadFromEnvProperty(AuthRequestRepository.class);
    }

    @Override
    public synchronized UserRepository userRepository() {
        return loadFromEnvProperty(UserRepository.class);
    }

    @Override
    public synchronized OauthSessionRepository oauthSessionRepository() {
        return loadFromEnvProperty(OauthSessionRepository.class);
    }

    @Override
    public AuthcodeRepository authcodeRepository() {
        return loadFromEnvProperty(AuthcodeRepository.class);
    }
    @Override
    public ParameterRepository parameterRepository() {
        return loadFromEnvProperty(ParameterRepository.class);
    }

    @Override
    public ExpiringKeypairRepository expiringKeypairRepository() {
        return loadFromEnvProperty(ExpiringKeypairRepository.class);
    }

    private static String getEnvProperty(String suffix) {
        return env(OIDC_CONFIGURATION_ROOT_PROPERTY + "_" + suffix);
    }
    // will return null if system property is empty/absent
    static <T> T loadClassFromEnvProperty(
            String suffix,
            Supplier<T> defaultValue
    ) {
        String className = getEnvProperty(suffix);
        if (className == null) {
            if (defaultValue != null) {
                T loaded = defaultValue.get();
                if (loaded != null) return loaded;
            }
            throw new RuntimeException("Property is not defined or has no value: " + OIDC_CONFIGURATION_ROOT_PROPERTY + "_" + suffix);
        }
        try {
            return (T)Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load class " + className, e);
        }
    }

    // no dots or dashes :/
    private Boolean getBooleanEnvProperty(String suffix) {
        String booleanValue = getEnvProperty(suffix);
        if (booleanValue == null) return null;
       return Boolean.valueOf(booleanValue);
    }

}
