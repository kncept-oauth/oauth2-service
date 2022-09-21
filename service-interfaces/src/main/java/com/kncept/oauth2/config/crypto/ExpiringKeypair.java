package com.kncept.oauth2.config.crypto;

import com.kncept.oauth2.config.annotation.OidcExpiryTime;
import com.kncept.oauth2.config.annotation.OidcId;

public interface ExpiringKeypair {

    @OidcId
    String id();

    String privateKey();

    String publicKey();

    long validFrom();

    long validTo();

    @OidcExpiryTime
    long deletionTime();

}
