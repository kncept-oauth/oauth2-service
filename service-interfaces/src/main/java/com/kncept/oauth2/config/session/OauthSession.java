package com.kncept.oauth2.config.session;

import com.kncept.oauth2.config.annotation.OidcExpiryTime;
import com.kncept.oauth2.config.annotation.OidcId;

import java.util.Optional;

public interface OauthSession {

    @OidcId String oauthSessionId();
    boolean authenticated();
    Optional<String> userId();

    @OidcExpiryTime long expiryTime();

}
