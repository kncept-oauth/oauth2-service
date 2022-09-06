package com.kncept.oauth2.config.authcode;

import com.kncept.oauth2.config.annotation.OidcExpiryTime;
import com.kncept.oauth2.config.annotation.OidcId;

public interface Authcode {

    @OidcId String authCode();
    String oauthSessionId();

    @OidcExpiryTime long expiryTime();
}
