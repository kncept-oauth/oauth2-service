package com.kncept.oauth2.config;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynoDbOauth2Configuration extends SingleStorageConfiguration implements Oauth2StorageConfiguration {

    private final boolean autocreateTables;

    private static boolean isAutocrate() {
        String value = System.getenv(EnvPropertyConfiguration.OIDC_STORAGE_CONFIGURATION_PROPERTY + "_Autocreate");
        if (value ==  null) return true;
        value = value.trim();
        if (value.equals("")) return true;
        return Boolean.parseBoolean(value);
    }

    public DynoDbOauth2Configuration(DynamoDbClient client) {
        this(client, isAutocrate());
    }

    public DynoDbOauth2Configuration(DynamoDbClient client, boolean autocreateTables) {
        super(new DynamoDbRepository(client, "KnceptOidc"));
        this.autocreateTables = autocreateTables;
    }
}
