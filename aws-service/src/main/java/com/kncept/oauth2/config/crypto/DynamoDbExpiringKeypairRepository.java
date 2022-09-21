package com.kncept.oauth2.config.crypto;

import com.kncept.oauth2.config.DynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;
import java.util.Optional;

import static com.kncept.oauth2.config.DynoDbOauth2Configuration.defaultTableName;

public class DynamoDbExpiringKeypairRepository extends DynamoDbRepository<ExpiringKeypair> implements ExpiringKeypairRepository {

    public DynamoDbExpiringKeypairRepository(DynamoDbClient client) {
        this(client, defaultTableName(ExpiringKeypairRepository.class));
    }

    public DynamoDbExpiringKeypairRepository(DynamoDbClient client, String tableName) {
        super(
                ExpiringKeypair.class,
                client,
                tableName
        );
    }

    @Override
    public Optional<ExpiringKeypair> lookup(String id) {
        return Optional.ofNullable(findById(id));
    }

    @Override
    public Optional<ExpiringKeypair> latest() {
        return Optional.empty();
    }

    @Override
    public void save(ExpiringKeypair kp) {
        write(kp);
    }

    @Override
    public List<ExpiringKeypair> all() {
        return list();
    }
}
