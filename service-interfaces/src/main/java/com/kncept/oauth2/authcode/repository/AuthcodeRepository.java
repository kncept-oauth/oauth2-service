package com.kncept.oauth2.authcode.repository;

import com.kncept.oauth2.authcode.Authcode;

import java.util.Optional;

public interface AuthcodeRepository {

    public Authcode create(String authCode, String oauthSessionId);

    public Optional<Authcode> lookup(String authCode);
}
