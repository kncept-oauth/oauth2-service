package com.kncept.oauth2.config.user;

public class SimpleUser implements User {

    private final String username;
    private String passhash;
    private final String userId;

    public SimpleUser(String userId, String username, String passhash) {
        this.userId = userId;
        this.username = username;
        this.passhash = passhash;
    }

    @Override
    public String username() {
        return username;
    }

    public String getPasshash() {
        return passhash;
    }

    @Override
    public String userId() {
        return userId;
    }
}