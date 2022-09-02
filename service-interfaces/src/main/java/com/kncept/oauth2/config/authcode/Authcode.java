package com.kncept.oauth2.config.authcode;

import com.kncept.oauth2.config.annotation.OidcIdField;

public interface Authcode {

    @OidcIdField String authCode();
    String oauthSessionId();
}
