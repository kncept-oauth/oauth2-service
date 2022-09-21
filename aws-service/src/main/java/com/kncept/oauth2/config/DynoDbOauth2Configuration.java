package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authcode.AuthcodeRepository;
import com.kncept.oauth2.config.authcode.DynamoDbAuthcodeRepository;
import com.kncept.oauth2.config.authrequest.AuthRequestRepository;
import com.kncept.oauth2.config.authrequest.DynamoDbAuthRequestRepository;
import com.kncept.oauth2.config.client.ClientRepository;
import com.kncept.oauth2.config.client.DynamoDbClientRepository;
import com.kncept.oauth2.config.crypto.DynamoDbExpiringKeypairRepository;
import com.kncept.oauth2.config.crypto.ExpiringKeypairRepository;
import com.kncept.oauth2.config.parameter.DynamoDbParameterRepository;
import com.kncept.oauth2.config.parameter.ParameterRepository;
import com.kncept.oauth2.config.session.DynamoDbOauthSessionRepository;
import com.kncept.oauth2.config.session.OauthSessionRepository;
import com.kncept.oauth2.config.user.DynamoDbUserRepository;
import com.kncept.oauth2.config.user.UserRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynoDbOauth2Configuration implements Oauth2Configuration {

    private final boolean autocreateTables;

    private DynamoDbClient client;

    private final EnvPropertyConfiguration config;

    private static boolean isAutocrate() {
        String value = System.getenv(OIDC_CONFIGURATION_ROOT_PROPERTY + "Autocreate");
        if (value ==  null) return true;
        value = value.trim();
        if (value.equals("")) return true;
        return Boolean.parseBoolean(value);
    }

    public DynoDbOauth2Configuration() {
        this(isAutocrate());
    }

    public DynoDbOauth2Configuration(boolean autocreateTables) {
        this.autocreateTables = autocreateTables;
        this.config = new EnvPropertyConfiguration();
    }

    public static String defaultTableName(Class interfaceType) {
        return "KnceptOidc" + interfaceType.getSimpleName();
    }

    public synchronized DynamoDbClient dynamoDbClient() {
        if (client == null) {
            client = DynamoDbClient.create();
        }
        return client;
    }

    @Override
    public ClientRepository clientRepository() {
        return config.loadFromEnvProperty(ClientRepository.class, () -> {
            DynamoDbClientRepository repo = new DynamoDbClientRepository(dynamoDbClient());
            if (autocreateTables) repo.createTableIfNotExists();
            return repo;
        });
    }

    @Override
    public AuthRequestRepository authRequestRepository() {
        return config.loadFromEnvProperty(AuthRequestRepository.class, () -> {
            DynamoDbAuthRequestRepository repo = new DynamoDbAuthRequestRepository(dynamoDbClient());
            if (autocreateTables) repo.createTableIfNotExists();
            return repo;
        });
    }

    @Override
    public AuthcodeRepository authcodeRepository() {
        return config.loadFromEnvProperty(AuthcodeRepository.class, () -> {
            DynamoDbAuthcodeRepository repo = new DynamoDbAuthcodeRepository(dynamoDbClient());
            if (autocreateTables) repo.createTableIfNotExists();
            return repo;
        });
    }

    @Override
    public UserRepository userRepository() {
        return config.loadFromEnvProperty(UserRepository.class, () -> {
            DynamoDbUserRepository repo = new DynamoDbUserRepository(dynamoDbClient());
            if (autocreateTables) repo.createTableIfNotExists();
            return repo;
        });
    }

    @Override
    public OauthSessionRepository oauthSessionRepository() {
        return config.loadFromEnvProperty(OauthSessionRepository.class, () -> {
            DynamoDbOauthSessionRepository repo = new DynamoDbOauthSessionRepository(dynamoDbClient());
            if (autocreateTables) repo.createTableIfNotExists();
            return repo;
        });
    }

    @Override
    public ParameterRepository parameterRepository() {
        return config.loadFromEnvProperty(ParameterRepository.class, () -> {
            DynamoDbParameterRepository repo = new DynamoDbParameterRepository(dynamoDbClient());
            if (autocreateTables) repo.createTableIfNotExists();
            return repo;
        });
    }

    @Override
    public ExpiringKeypairRepository expiringKeypairRepository() {
        return config.loadFromEnvProperty(ExpiringKeypairRepository.class, () -> {
            DynamoDbExpiringKeypairRepository repo = new DynamoDbExpiringKeypairRepository(dynamoDbClient());
            if (autocreateTables) repo.createTableIfNotExists();
            return repo;
        });
    }
}
