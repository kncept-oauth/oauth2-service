package com.kncept.oauth2.configuration;

import com.kncept.oauth2.authcode.AuthcodeRepository;
import com.kncept.oauth2.authcode.InMemoryAuthcodeRepository;
import com.kncept.oauth2.authrequest.AuthRequestRepository;
import com.kncept.oauth2.authrequest.InMemoryAuthRequestRepository;
import com.kncept.oauth2.client.AnyClientRepository;
import com.kncept.oauth2.client.ClientRepository;
import com.kncept.oauth2.config.Oauth2Configuration;
import com.kncept.oauth2.session.InMemoryOauthSessionRepository;
import com.kncept.oauth2.session.OauthSessionRepository;
import com.kncept.oauth2.user.InMemoryUserRepository;
import com.kncept.oauth2.user.UserRepository;

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
