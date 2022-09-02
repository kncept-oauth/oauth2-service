package com.kncept.oauth2.authcode;

import java.util.Optional;

public interface AuthcodeRepository {

    public Authcode create(String authCode, String oauthSessionId);

    public Optional<Authcode> lookup(String authCode);
}
