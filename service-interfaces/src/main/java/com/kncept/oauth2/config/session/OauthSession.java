package com.kncept.oauth2.config.session;

import java.util.Optional;

public interface OauthSession {

    String oauthSessionId();

    boolean authenticated();
    Optional<String> userId();

}
