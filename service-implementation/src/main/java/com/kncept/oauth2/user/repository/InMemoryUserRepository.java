package com.kncept.oauth2.user.repository;

import com.kncept.oauth2.user.User;

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

    private final Map<String, SimpleUser> users = new HashMap<>();

    public InMemoryUserRepository() {
    }

    @Override
    public boolean isAcceptingSignup() {
        return true;
    }

    @Override
    public User lookupUser(String username, String password) {
        String passhash = hash(username, password);
        SimpleUser user = users.get(username);
        if (user != null && user.getPasshash().equals(passhash)) return user;
        return null;
    }

    @Override
    public User createUser(String username, String password) {
        if (password == null) return null;
        password = password.trim();
        if (password.length() < 3) return null;
        if (users.containsKey(username)) return null;
        SimpleUser user = new SimpleUser(username, hash(username, password));
        users.put(username, user);
        return user;
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
        public String username() {
            return username;
        }

        public String getPasshash() {
            return passhash;
        }

        @Override
        public String sub() {
            return sub;
        }
    }
}
