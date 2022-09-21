package com.kncept.oauth2.config.user;

import com.kncept.oauth2.config.DynamoDbRepository;
import com.kncept.oauth2.crypto.auth.AuthCrypto;
import com.kncept.oauth2.crypto.auth.PasswordHasher;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;

import static com.kncept.oauth2.config.DynoDbOauth2Configuration.defaultTableName;

public class DynamoDbUserRepository extends DynamoDbRepository<SaltedUser> implements UserRepository {
    private AuthCrypto crypto;

    public DynamoDbUserRepository(DynamoDbClient client) {
        super(
                SaltedUser.class,
                client,
                defaultTableName(UserRepository.class)
        );
        crypto = new AuthCrypto();
    }

    public DynamoDbUserRepository(DynamoDbClient client, String tableName) {
        super(
                SaltedUser.class,
                client,
                tableName
        );
        crypto = new AuthCrypto();
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
