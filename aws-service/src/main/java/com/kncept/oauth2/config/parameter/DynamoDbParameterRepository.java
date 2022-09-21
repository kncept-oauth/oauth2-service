package com.kncept.oauth2.config.parameter;

import com.kncept.oauth2.config.DynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDbParameterRepository extends DynamoDbRepository<Parameter> implements ParameterRepository {

    public DynamoDbParameterRepository(DynamoDbClient client, String tableName) {
        super(
                Parameter.class,
                client,
                tableName
        );
    }

    @Override
    public Parameter getParameter(String name) {
        return findById(name);
    }
}
