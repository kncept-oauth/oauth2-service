package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authcode.AuthcodeRepository;
import com.kncept.oauth2.config.authrequest.AuthRequestRepository;
import com.kncept.oauth2.config.client.ClientRepository;
import com.kncept.oauth2.config.parameter.ParameterRepository;
import com.kncept.oauth2.config.session.OauthSessionRepository;
import com.kncept.oauth2.config.user.UserRepository;

public class SystemProperyConfiguration implements Oauth2Configuration {

    private Boolean requirePkce;
    private ClientRepository clientRepository;
    private AuthRequestRepository authRequestRepository;
    private UserRepository userRepository;
    private OauthSessionRepository oauthSessionRepository;
    private AuthcodeRepository authcodeRepository;
    private ParameterRepository parameterRepository;

    @Override
    public synchronized ClientRepository clientRepository() {
        if (clientRepository == null) {
            clientRepository = loadClassFromEnvProperty("clients", ClientRepository.class);
        }
        return clientRepository;
    }

    @Override
    public synchronized AuthRequestRepository authRequestRepository() {
        if (authRequestRepository == null) {
            authRequestRepository = loadClassFromEnvProperty("authrequests", AuthRequestRepository.class);
        }
        return authRequestRepository;
    }

    @Override
    public synchronized UserRepository userRepository() {
        if (userRepository == null) {
            userRepository = loadClassFromEnvProperty("users", UserRepository.class);
        }
        return userRepository;
    }

    @Override
    public synchronized OauthSessionRepository oauthSessionRepository() {
        if (oauthSessionRepository == null) {
            oauthSessionRepository = loadClassFromEnvProperty("sessions", OauthSessionRepository.class);
        }
        return oauthSessionRepository;
    }

    @Override
    public AuthcodeRepository authcodeRepository() {
        if (authcodeRepository == null) {
            authcodeRepository = loadClassFromEnvProperty("authcodes", AuthcodeRepository.class);
        }
        return authcodeRepository;
    }
    @Override
    public ParameterRepository parameterRepository() {
        if (parameterRepository == null) {
            parameterRepository = loadClassFromEnvProperty("parameter", ParameterRepository.class);
        }
        return parameterRepository;
    }

    private static String getEnvProperty(String suffix) {
        String value = System.getenv(OIDC_CONFIGURATION_ROOT_PROPERTY + "_" + suffix);
        if (value == null) return null;
        value = value.trim();
        if (value.equals("")) return null;
        return value;
    }
    // will return null if system property is empty/absent
    private static <T> T loadClassFromEnvProperty(
            String propertyName,
            Class<T> type
    ) {
        String className = getEnvProperty(propertyName);
        if (className == null) throw new NullPointerException();
        try {
            Object configuredImpl = Thread.currentThread().getContextClassLoader().loadClass(className);
            if(type.isAssignableFrom(configuredImpl.getClass()))
                return (T)configuredImpl;
            throw new RuntimeException(className + " is not a " + type.getName());
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
