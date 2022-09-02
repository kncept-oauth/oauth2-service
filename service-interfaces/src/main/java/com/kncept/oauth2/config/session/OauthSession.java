package com.kncept.oauth2.config.session;

import com.kncept.oauth2.config.annotation.OidcIdField;

import java.util.Optional;

public interface OauthSession {

    @OidcIdField String oauthSessionId();
    boolean authenticated();
    Optional<String> userId();

}
