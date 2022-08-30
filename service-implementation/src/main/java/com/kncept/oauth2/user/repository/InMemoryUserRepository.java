package com.kncept.oauth2.user.repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class really really REALLY shouldn't be used in production.
 */
public class InMemoryUserRepository implements UserRepository {

    private final String randomSalt = UUID.randomUUID().toString();

    private final Map<String, String> userToPasswordHash = new HashMap<>();

    public InMemoryUserRepository() {
        createUser("test", "test");
    }

    @Override
    public boolean isAcceptingSignup() {
        return true;
    }

    @Override
    public boolean verifyUser(String username, String password) {
        String passwordHash = hash(username, password);
        return passwordHash.equals(userToPasswordHash.getOrDefault(username, "_"));
    }

    @Override
    public boolean createUser(String username, String password) {
        if (password == null) return false;
        password = password.trim();
        if (password.length() < 3) return false;
        if (userToPasswordHash.containsKey(username)) return false;
        userToPasswordHash.put(username, hash(username, password));
        return true;
    }

    // half of a hashing algorithm
    private String hash(String username, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(randomSalt.getBytes());
            md.update(username.getBytes());
            md.update(password.getBytes());

            byte[] simpleHash = md.digest();
            return Base64.getEncoder().encodeToString(simpleHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
