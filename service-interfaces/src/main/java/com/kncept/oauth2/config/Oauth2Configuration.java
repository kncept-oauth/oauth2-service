package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authcode.AuthcodeRepository;
import com.kncept.oauth2.config.authrequest.AuthRequestRepository;
import com.kncept.oauth2.config.client.ClientRepository;
import com.kncept.oauth2.config.session.OauthSessionRepository;
import com.kncept.oauth2.config.user.UserRepository;

public interface Oauth2Configuration {
        boolean requirePkce();
        ClientRepository clientRepository();
        AuthRequestRepository authRequestRepository();
        AuthcodeRepository authcodeRepository();
        UserRepository userRepository();
        OauthSessionRepository oauthSessionRepository();
}
