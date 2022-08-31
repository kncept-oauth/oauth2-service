package com.kncept.oauth2.user;

public interface User {
    String getUsername();
    String getSub(); // content of SUB claim, the USER ID
}
