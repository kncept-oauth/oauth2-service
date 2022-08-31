package com.kncept.oauth2.user;

public interface User {
    String username();
    String sub(); // content of SUB (Subject) claim, the USER ID
}
