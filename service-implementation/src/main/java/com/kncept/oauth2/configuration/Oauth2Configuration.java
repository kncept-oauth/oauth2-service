package com.kncept.oauth2.configuration;

import com.kncept.oauth2.authrequest.repository.AuthRequestRepository;
import com.kncept.oauth2.authrequest.repository.InMemoryAuthRequestRepository;
import com.kncept.oauth2.client.repository.AnyClientRepository;
import com.kncept.oauth2.client.repository.ClientRepository;
import com.kncept.oauth2.html.HtmlPageVendor;
import com.kncept.oauth2.session.repository.InMemoryOauthSessionRepository;
import com.kncept.oauth2.session.repository.OauthSessionRepository;
import com.kncept.oauth2.user.repository.InMemoryUserRepository;
import com.kncept.oauth2.user.repository.UserRepository;

public class Oauth2Configuration {

    private Boolean requirePkce;
    private ClientRepository clientRepository;
    private AuthRequestRepository authRequestRepository;
    private UserRepository userRepository;
    private OauthSessionRepository oauthSessionRepository;

    // TODO: config-ify this
    private HtmlPageVendor htmlPageVendor = new HtmlPageVendor();

    public synchronized boolean requirePkce() {
        if (requirePkce == null) {
            requirePkce = getBooleanSystemProperty("oauth2.require-pkce");
            if (requirePkce == null) requirePkce = false;
        }
        return requirePkce;
    }

    public synchronized ClientRepository clientRepository() {
        if (clientRepository == null) {
            clientRepository = loadClassFromSystemProperty("oauth2.client-repository", ClientRepository.class);
            if (clientRepository == null) {
                clientRepository = new AnyClientRepository();
            }
        }
        return clientRepository;
    }

    public synchronized AuthRequestRepository authRequestRepository() {
        if (authRequestRepository == null) {
            authRequestRepository = loadClassFromSystemProperty("oauth2.authrequest-repository", AuthRequestRepository.class);
            if (authRequestRepository == null) {
                authRequestRepository = new InMemoryAuthRequestRepository();
            }
        }
        return authRequestRepository;
    }

    public synchronized UserRepository userRepository() {
        if (userRepository == null) {
            userRepository = loadClassFromSystemProperty("oauth2.user-repository", UserRepository.class);
            if (userRepository == null) {
                userRepository = new InMemoryUserRepository();
            }
        }
        return userRepository;
    }

    public synchronized OauthSessionRepository oauthSessionRepository() {
        if (oauthSessionRepository == null) {
            oauthSessionRepository = loadClassFromSystemProperty("oauth2.session-repository", OauthSessionRepository.class);
            if (oauthSessionRepository == null) {
                oauthSessionRepository = new InMemoryOauthSessionRepository();
            }
        }
        return oauthSessionRepository;
    }



    public HtmlPageVendor htmlPageVendor() {
        return htmlPageVendor;
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
        if (className == null) return null;
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
