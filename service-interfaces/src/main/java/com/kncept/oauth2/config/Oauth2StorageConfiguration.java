package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authrequest.AuthRequest;
import com.kncept.oauth2.config.client.Client;
import com.kncept.oauth2.config.crypto.ExpiringKeypair;
import com.kncept.oauth2.config.parameter.Parameter;
import com.kncept.oauth2.config.session.OauthSession;
import com.kncept.oauth2.config.user.User;
import com.kncept.oauth2.config.user.UserLogin;

public interface Oauth2StorageConfiguration {

        SimpleCrudRepository<Client> clientRepository();
        SimpleCrudRepository<AuthRequest> authRequestRepository();
        SimpleCrudRepository<User> userRepository();
        SimpleCrudRepository<UserLogin> userLoginRepository();
        SimpleCrudRepository<OauthSession> oauthSessionRepository();
        SimpleCrudRepository<Parameter> parameterRepository(); // <-- TODO: Not STORAGE config (!!)
        SimpleCrudRepository<ExpiringKeypair> expiringKeypairRepository();
}

