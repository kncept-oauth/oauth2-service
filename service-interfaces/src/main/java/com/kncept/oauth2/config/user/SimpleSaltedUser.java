package com.kncept.oauth2.config.user;

public class SimpleSaltedUser extends SimpleUser implements SaltedUser {

    private String salt;
    private String passwordHash;
    private String hashAlgorithm;

    public SimpleSaltedUser(String userId, String username, String salt, String passwordHash, String hashAlgorithm) {
        super(userId, username);
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.hashAlgorithm = hashAlgorithm;
    }

    @Override
    public String salt() {
        return salt;
    }

    @Override
    public String passwordHash() {
        return passwordHash;
    }

    @Override
    public String hashAlgorithm() {
        return hashAlgorithm;
    }
}