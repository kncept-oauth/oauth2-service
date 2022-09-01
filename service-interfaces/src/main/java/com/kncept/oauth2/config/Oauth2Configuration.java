package com.kncept.oauth2.config;

import com.kncept.oauth2.authrequest.repository.AuthRequestRepository;
import com.kncept.oauth2.client.repository.ClientRepository;
import com.kncept.oauth2.session.repository.OauthSessionRepository;
import com.kncept.oauth2.user.repository.UserRepository;

public interface Oauth2Configuration {
        boolean requirePkce();
        ClientRepository clientRepository();
        AuthRequestRepository authRequestRepository();
        UserRepository userRepository();
        OauthSessionRepository oauthSessionRepository();
}
