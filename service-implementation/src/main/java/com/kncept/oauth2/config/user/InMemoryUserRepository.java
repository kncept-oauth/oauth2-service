package com.kncept.oauth2.config.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * This class really really REALLY shouldn't be used in production.
 */
public class InMemoryUserRepository implements UserRepository {

    private final String randomSalt = UUID.randomUUID().toString();

    private final Map<String, SimpleUser> users = new HashMap<>();

    public InMemoryUserRepository() {
    }

    @Override
    public boolean acceptingSignup() {
        return true;
    }

    @Override
    public Optional<User> login(String username, String password) {
        String passhash = hash(username, password);
        SimpleUser user = users.get(username);
        if (user != null && user.getPasshash().equals(passhash)) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public Optional<User> create(String username, String password) {
        if (password == null) return Optional.empty();
        password = password.trim();
        if (password.length() < 3) return Optional.empty();
        if (users.containsKey(username)) return Optional.empty();
        SimpleUser user = new SimpleUser(username, hash(username, password));
        users.put(username, user);
        return Optional.of(user);
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

    private static class SimpleUser implements User {

        private final String username;
        private String passhash;
        private final String userId;

        public SimpleUser(String username, String passhash) {
            this(username, passhash, UUID.randomUUID().toString());
        }
        public SimpleUser(String username, String passhash, String userId) {
            this.username = username;
            this.passhash = passhash;
            this.userId = userId;
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
}
