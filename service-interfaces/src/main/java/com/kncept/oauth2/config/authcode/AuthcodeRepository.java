package com.kncept.oauth2.config.authcode;

import java.util.Optional;

public interface AuthcodeRepository {

    Authcode create(String authCode, String oauthSessionId);

    Optional<Authcode> lookup(String authCode);
}
