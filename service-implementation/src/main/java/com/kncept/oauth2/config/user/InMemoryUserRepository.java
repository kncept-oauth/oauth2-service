package com.kncept.oauth2.config.user;

import com.kncept.oauth2.crypto.auth.AuthCrypto;
import com.kncept.oauth2.crypto.auth.PasswordHasher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class really really REALLY shouldn't be used in production.
 */
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, SimpleSaltedUser> users = new HashMap<>();

    private final AuthCrypto crypto = new AuthCrypto();

    public InMemoryUserRepository() {
    }
    @Override
    public Optional<User> login(String username, String password) {
        SimpleSaltedUser user = users.get(username);
        if (user == null) return Optional.empty();
        String passhash = crypto.hasher(user.hashAlgorithm()).hash(password, user.salt());
        if (user.passwordHash().equals(passhash)) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public Optional<User> create(String username, String password) {
        if (password == null) return Optional.empty();
        password = password.trim();
        if (password.length() < 3) return Optional.empty();
        if (users.containsKey(username)) return Optional.empty();
        String salt = crypto.salt();
        PasswordHasher hasher = crypto.hasher();
        String passhash = hasher.hash(password, salt);
        SimpleSaltedUser user = new SimpleSaltedUser(username, username, salt, passhash, hasher.algorithm());
        users.put(username, user);
        return Optional.of(user);
    }

}
