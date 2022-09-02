package com.kncept.oauth2.configuration;

import com.kncept.oauth2.authcode.repository.AuthcodeRepository;
import com.kncept.oauth2.authcode.repository.InMemoryAuthcodeRepository;
import com.kncept.oauth2.authrequest.repository.AuthRequestRepository;
import com.kncept.oauth2.authrequest.repository.InMemoryAuthRequestRepository;
import com.kncept.oauth2.client.repository.AnyClientRepository;
import com.kncept.oauth2.client.repository.ClientRepository;
import com.kncept.oauth2.config.Oauth2Configuration;
import com.kncept.oauth2.session.repository.InMemoryOauthSessionRepository;
import com.kncept.oauth2.session.repository.OauthSessionRepository;
import com.kncept.oauth2.user.repository.InMemoryUserRepository;
import com.kncept.oauth2.user.repository.UserRepository;

public class InMemoryConfiguration implements Oauth2Configuration {

    private Boolean requirePkce;
    private ClientRepository clientRepository;
    private AuthRequestRepository authRequestRepository;
    private UserRepository userRepository;
    private OauthSessionRepository oauthSessionRepository;
    private AuthcodeRepository authcodeRepository;

    public InMemoryConfiguration() {
        requirePkce = false;
    }

    public InMemoryConfiguration(boolean requirePkce) {
        this.requirePkce = requirePkce;
    }

    @Override
    public synchronized boolean requirePkce() {
        return requirePkce;
    }

    @Override
    public synchronized ClientRepository clientRepository() {
        if (clientRepository == null) {
            clientRepository = new AnyClientRepository();
        }
        return clientRepository;
    }

    @Override
    public synchronized AuthRequestRepository authRequestRepository() {
        if (authRequestRepository == null) {
            authRequestRepository = new InMemoryAuthRequestRepository();
        }
        return authRequestRepository;
    }

    @Override
    public synchronized UserRepository userRepository() {
        if (userRepository == null) {
            userRepository = new InMemoryUserRepository();
        }
        return userRepository;
    }

    @Override
    public synchronized OauthSessionRepository oauthSessionRepository() {
        if (oauthSessionRepository == null) {
            oauthSessionRepository = new InMemoryOauthSessionRepository();
        }
        return oauthSessionRepository;
    }

    @Override
    public AuthcodeRepository authcodeRepository() {
        if (authcodeRepository == null) {
            authcodeRepository = new InMemoryAuthcodeRepository();
        }
        return authcodeRepository;
    }
}
