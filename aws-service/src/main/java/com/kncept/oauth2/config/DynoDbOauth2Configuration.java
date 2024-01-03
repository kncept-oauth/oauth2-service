package com.kncept.oauth2.config;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynoDbOauth2Configuration extends SingleStorageConfiguration implements Oauth2StorageConfiguration {


    public DynoDbOauth2Configuration() {
        this(DynamoDbClient.create());
    }

    public DynoDbOauth2Configuration(DynamoDbClient client) {
        super(new DynamoDbRepository(client, "SimpleOidc"));
    }
}
