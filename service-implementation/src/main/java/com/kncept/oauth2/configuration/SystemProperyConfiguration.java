package com.kncept.oauth2.configuration;

import com.kncept.oauth2.authcode.repository.AuthcodeRepository;
import com.kncept.oauth2.authrequest.repository.AuthRequestRepository;
import com.kncept.oauth2.client.repository.ClientRepository;
import com.kncept.oauth2.config.Oauth2Configuration;
import com.kncept.oauth2.session.repository.OauthSessionRepository;
import com.kncept.oauth2.user.repository.UserRepository;

public class SystemProperyConfiguration implements Oauth2Configuration {

    private Boolean requirePkce;
    private ClientRepository clientRepository;
    private AuthRequestRepository authRequestRepository;
    private UserRepository userRepository;
    private OauthSessionRepository oauthSessionRepository;
    private AuthcodeRepository authcodeRepository;

    @Override
    public synchronized boolean requirePkce() {
        if (requirePkce == null) {
            requirePkce = getBooleanSystemProperty("oauth2.require-pkce");
            if (requirePkce == null) throw new NullPointerException();
        }
        return requirePkce;
    }

    @Override
    public synchronized ClientRepository clientRepository() {
        if (clientRepository == null) {
            clientRepository = loadClassFromSystemProperty("oauth2.client-repository", ClientRepository.class);
        }
        return clientRepository;
    }

    @Override
    public synchronized AuthRequestRepository authRequestRepository() {
        if (authRequestRepository == null) {
            authRequestRepository = loadClassFromSystemProperty("oauth2.authrequest-repository", AuthRequestRepository.class);
        }
        return authRequestRepository;
    }

    @Override
    public synchronized UserRepository userRepository() {
        if (userRepository == null) {
            userRepository = loadClassFromSystemProperty("oauth2.user-repository", UserRepository.class);
        }
        return userRepository;
    }

    @Override
    public synchronized OauthSessionRepository oauthSessionRepository() {
        if (oauthSessionRepository == null) {
            oauthSessionRepository = loadClassFromSystemProperty("oauth2.session-repository", OauthSessionRepository.class);
        }
        return oauthSessionRepository;
    }

    @Override
    public AuthcodeRepository authcodeRepository() {
        if (authcodeRepository == null) {
            authcodeRepository = loadClassFromSystemProperty("oauth2.authcode-repository", AuthcodeRepository.class);
        }
        return authcodeRepository;
    }

    private static String getSystemProperty(String name) {
        String value = System.getProperty(name);
        if (value == null) return null;
        value = value.trim();
        if (value.equals("")) return null;
        return value;
    }
    // will return null if system property is empty/absent
    public static <T> T loadClassFromSystemProperty(
            String propertyName,
            Class<T> type
    ) {
        String className = getSystemProperty(propertyName);
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

    private Boolean getBooleanSystemProperty(String propertyName) {
        String booleanValue = getSystemProperty(propertyName);
        if (booleanValue == null) return null;
       return Boolean.valueOf(booleanValue);
    }

}
