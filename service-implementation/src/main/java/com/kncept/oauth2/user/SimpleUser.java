package com.kncept.oauth2.user;

import java.util.UUID;

public class SimpleUser implements User {

    private final String username;
    private String passhash;
    private final String sub;

    public SimpleUser(String username, String passhash) {
        this(username, passhash, UUID.randomUUID().toString());
    }
    public SimpleUser(String username, String passhash, String sub) {
        this.username = username;
        this.passhash = passhash;
        this.sub = sub;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getPasshash() {
        return passhash;
    }

    @Override
    public String getSub() {
        return sub;
    }
}
