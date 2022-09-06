package com.kncept.oauth2.config.user;

import com.kncept.oauth2.config.annotation.OidcId;

public interface User {
    @OidcId String userId(); // content of SUB (Subject) claim, the USER ID
    String username();
}
