package com.kncept.oauth2.config.client;

import com.kncept.oauth2.config.DynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;

public class DynamoDbClientRepository extends DynamoDbRepository<Client> implements ClientRepository {

    public DynamoDbClientRepository(DynamoDbClient client, String tableName) {
        super(
                Client.class,
                client,
                tableName
        );
    }

    @Override
    public Optional<Client> lookup(String clientId) {
        return Optional.ofNullable(findById(clientId));
    }

    @Override
    public Optional<Client> update(Client client) {
        write(client);
        return Optional.of(client);
    }
}
