package com.kncept.oauth2.user;

public interface User {
    String username();
    String userId(); // content of SUB (Subject) claim, the USER ID
}
