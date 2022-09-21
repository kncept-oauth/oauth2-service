package com.kncept.oauth2.config.parameter;

import com.kncept.oauth2.config.annotation.OidcId;

public interface Parameter {

    @OidcId
    String name();

    String value();
}
