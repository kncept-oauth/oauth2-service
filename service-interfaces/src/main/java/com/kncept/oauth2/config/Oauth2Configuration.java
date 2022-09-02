package com.kncept.oauth2.config;

import com.kncept.oauth2.authcode.AuthcodeRepository;
import com.kncept.oauth2.authrequest.AuthRequestRepository;
import com.kncept.oauth2.client.ClientRepository;
import com.kncept.oauth2.session.OauthSessionRepository;
import com.kncept.oauth2.user.UserRepository;

public interface Oauth2Configuration {
        boolean requirePkce();
        ClientRepository clientRepository();
        AuthRequestRepository authRequestRepository();
        AuthcodeRepository authcodeRepository();
        UserRepository userRepository();
        OauthSessionRepository oauthSessionRepository();
}
