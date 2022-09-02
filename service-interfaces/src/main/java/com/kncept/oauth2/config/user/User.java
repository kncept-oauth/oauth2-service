package com.kncept.oauth2.config.user;

public interface User {
    String username();
    String userId(); // content of SUB (Subject) claim, the USER ID
}
