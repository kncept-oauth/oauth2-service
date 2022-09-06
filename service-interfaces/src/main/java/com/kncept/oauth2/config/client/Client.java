package com.kncept.oauth2.config.client;

import com.kncept.oauth2.config.annotation.OidcId;

public interface Client {
    @OidcId String clientId();
    boolean enabled();
}
