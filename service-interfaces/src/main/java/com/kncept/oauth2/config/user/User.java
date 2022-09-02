package com.kncept.oauth2.config.user;

import com.kncept.oauth2.config.annotation.OidcIdField;

public interface User {
    @OidcIdField String username();
    String userId(); // content of SUB (Subject) claim, the USER ID
}
