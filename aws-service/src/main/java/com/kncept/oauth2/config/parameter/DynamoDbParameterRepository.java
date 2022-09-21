package com.kncept.oauth2.config.parameter;

import com.kncept.oauth2.config.DynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static com.kncept.oauth2.config.DynoDbOauth2Configuration.defaultTableName;

public class DynamoDbParameterRepository extends DynamoDbRepository<Parameter> implements ParameterRepository {

    public DynamoDbParameterRepository(DynamoDbClient client) {
        super(
                Parameter.class,
                client,
                defaultTableName(ParameterRepository.class)
        );
    }

    public DynamoDbParameterRepository(DynamoDbClient client, String tableName) {
        super(
                Parameter.class,
                client,
                tableName
        );
    }

    @Override
    public Parameter parameter(String name) {
        return findById(name);
    }
}
