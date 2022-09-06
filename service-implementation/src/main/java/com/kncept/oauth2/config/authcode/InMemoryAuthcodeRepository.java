package com.kncept.oauth2.config.authcode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAuthcodeRepository implements AuthcodeRepository {

    private final Map<String, Authcode> codes = new HashMap<>();

    @Override
    public Authcode create(String authCode, String oauthSessionId) {
        Authcode code = new SimpleAuthcode(authCode, oauthSessionId, 0);
        codes.put(authCode, code);
        return code;
    }

    @Override
    public Optional<Authcode> lookup(String authCode) {
        return Optional.ofNullable(codes.get(authCode));
    }
}
