package com.kncept.oauth2.config.user;

public class SimpleUser implements User {

    private final String username;
    private final String userId;

    public SimpleUser(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public String userId() {
        return userId;
    }
}