package com.kncept.oauth2.config.user;

import com.kncept.oauth2.config.DynamoDbRepository;
import com.kncept.oauth2.crypto.auth.AuthCrypto;
import com.kncept.oauth2.crypto.auth.PasswordHasher;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;

public class DynamoDbUserRepository extends DynamoDbRepository<SaltedUser> implements UserRepository {

    private boolean acceptingSignup;
    private AuthCrypto crypto;

    public DynamoDbUserRepository(DynamoDbClient client, String tableName, boolean acceptingSignup) {
        super(
        		SaltedUser.class,
                client,
                tableName
        );
        this.acceptingSignup = acceptingSignup;
        crypto = new AuthCrypto();
    }

    @Override
    public boolean acceptingSignup() {
        return acceptingSignup;
    }

    @Override
    public Optional<User> login(String username, String password) {
        SaltedUser user = findById(username);
        if (user == null) return Optional.empty();
        String passhash = crypto.hasher(user.hashAlgorithm()).hash(password, user.salt());
        if (passhash.equals(user.passwordHash())) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public Optional<User> create(String username, String password) {
        if (findById(username) != null) return Optional.empty();
        String salt = crypto.salt();
        PasswordHasher hasher = crypto.hasher();
        String passhash = hasher.hash(password, salt);
        SaltedUser user = new SimpleSaltedUser(username, username, salt, passhash, hasher.algorithm());
        write(user);
        return Optional.of(user);
    }

}
