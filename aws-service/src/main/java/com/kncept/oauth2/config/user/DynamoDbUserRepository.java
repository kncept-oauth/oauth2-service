package com.kncept.oauth2.config.user;

import com.kncept.oauth2.config.DynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;

//TODO: Implement passwords and so on. ugh. sheesh.
public class DynamoDbUserRepository extends DynamoDbRepository<User> implements UserRepository {

    private boolean acceptingSignup;

    public DynamoDbUserRepository(DynamoDbClient client, String tableName, boolean acceptingSignup) {
        super(
                User.class,
                client,
                tableName
        );
        this.acceptingSignup = acceptingSignup;
    }

    @Override
    public boolean acceptingSignup() {
        return acceptingSignup;
    }

    @Override
    public Optional<User> login(String username, String password) {
        String passhash = "none";
        User user = findById(username);
        if (user == null) return Optional.empty();

        return Optional.empty();
    }

    @Override
    public Optional<User> create(String username, String password) {
        if (findById(username) != null) return Optional.empty();
        User user = new SimpleUser(username, username, "TODO: passhash");
        write(username, user);
        return Optional.of(user);
    }
}
