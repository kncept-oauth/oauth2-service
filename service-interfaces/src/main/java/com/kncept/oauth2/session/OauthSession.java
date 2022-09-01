package com.kncept.oauth2.session;

import java.util.Optional;

public interface OauthSession {

    String oauthSessionId();

    boolean authenticated();
    Optional<String> userId();

}
