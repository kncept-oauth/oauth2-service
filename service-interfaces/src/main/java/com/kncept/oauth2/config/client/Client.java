package com.kncept.oauth2.config.client;

import com.kncept.oauth2.config.annotation.OidcIdField;

public interface Client {
    @OidcIdField String clientId();
    boolean enabled();
}
