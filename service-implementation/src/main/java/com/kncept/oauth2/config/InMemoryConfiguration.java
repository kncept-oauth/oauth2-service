package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authcode.InMemoryAuthcodeRepository;
import com.kncept.oauth2.config.authrequest.InMemoryAuthRequestRepository;
import com.kncept.oauth2.config.client.InMemoryClientRepository;
import com.kncept.oauth2.config.session.InMemoryOauthSessionRepository;
import com.kncept.oauth2.config.user.InMemoryUserRepository;

public class InMemoryConfiguration implements Oauth2Configuration {

    private Boolean requirePkce;
    private InMemoryClientRepository clientRepository;
    private InMemoryAuthRequestRepository authRequestRepository;
    private InMemoryUserRepository userRepository;
    private InMemoryOauthSessionRepository oauthSessionRepository;
    private InMemoryAuthcodeRepository authcodeRepository;

    public InMemoryConfiguration() {
        requirePkce = true;
    }

    public InMemoryConfiguration(boolean requirePkce) {
        this.requirePkce = requirePkce;
    }

    @Override
    public synchronized boolean requirePkce() {
        return requirePkce;
    }

    @Override
    public synchronized InMemoryClientRepository clientRepository() {
        if (clientRepository == null) {
            clientRepository = new InMemoryClientRepository();
        }
        return clientRepository;
    }

    @Override
    public synchronized InMemoryAuthRequestRepository authRequestRepository() {
        if (authRequestRepository == null) {
            authRequestRepository = new InMemoryAuthRequestRepository();
        }
        return authRequestRepository;
    }

    @Override
    public synchronized InMemoryUserRepository userRepository() {
        if (userRepository == null) {
            userRepository = new InMemoryUserRepository();
        }
        return userRepository;
    }

    @Override
    public synchronized InMemoryOauthSessionRepository oauthSessionRepository() {
        if (oauthSessionRepository == null) {
            oauthSessionRepository = new InMemoryOauthSessionRepository();
        }
        return oauthSessionRepository;
    }

    @Override
    public synchronized InMemoryAuthcodeRepository authcodeRepository() {
        if (authcodeRepository == null) {
            authcodeRepository = new InMemoryAuthcodeRepository();
        }
        return authcodeRepository;
    }
}
