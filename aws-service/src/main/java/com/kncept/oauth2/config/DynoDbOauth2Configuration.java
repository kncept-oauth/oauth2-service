package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authcode.AuthcodeRepository;
import com.kncept.oauth2.config.authcode.DynamoDbAuthcodeRepository;
import com.kncept.oauth2.config.authrequest.AuthRequestRepository;
import com.kncept.oauth2.config.authrequest.DynamoDbAuthRequestRepository;
import com.kncept.oauth2.config.client.ClientRepository;
import com.kncept.oauth2.config.client.DynamoDbClientRepository;
import com.kncept.oauth2.config.session.DynamoDbOauthSessionRepository;
import com.kncept.oauth2.config.session.OauthSessionRepository;
import com.kncept.oauth2.config.user.DynamoDbUserRepository;
import com.kncept.oauth2.config.user.UserRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynoDbOauth2Configuration implements Oauth2Configuration {

    private final boolean requirePkce;
    private final boolean acceptingSignup;
    private final boolean autocreateTables;

    private DynamoDbClient client;

    private DynamoDbClientRepository clientRepository;
    private DynamoDbAuthRequestRepository authRequestRepository;
    private DynamoDbAuthcodeRepository authcodeRepository;
    private DynamoDbUserRepository userRepository;
    private DynamoDbOauthSessionRepository oauthSessionRepository;

    public DynoDbOauth2Configuration() {
        this(false, true, true);
    }

    public DynoDbOauth2Configuration(boolean requirePkce, boolean acceptingSignup, boolean autocreateTables) {
        this.requirePkce = requirePkce;
        this.acceptingSignup = acceptingSignup;
        this.autocreateTables = autocreateTables;
    }

    @Override
    public boolean requirePkce() {
        return requirePkce;
    }

    public String tableName(Class interfaceType) {
        return "KnceptOidc" + interfaceType.getSimpleName();
    }

    public DynamoDbClient dynamoDbClient() {
        if (client == null) {
            client = DynamoDbClient.create();
        }
        return client;
    }

    @Override
    public DynamoDbClientRepository clientRepository() {
        if (clientRepository == null) {
            clientRepository = new DynamoDbClientRepository(
                    dynamoDbClient(),
                    tableName(ClientRepository.class)
            );
            if (autocreateTables) clientRepository.createTableIfNotExists();
        }
        return clientRepository;
    }

    @Override
    public DynamoDbAuthRequestRepository authRequestRepository() {
        if (authRequestRepository == null) {
            authRequestRepository = new DynamoDbAuthRequestRepository(
                    dynamoDbClient(),
                    tableName(AuthRequestRepository.class)
            );
            if (autocreateTables) authRequestRepository.createTableIfNotExists();
        }
        return authRequestRepository;
    }

    @Override
    public DynamoDbAuthcodeRepository authcodeRepository() {
        if (authcodeRepository == null) {
            authcodeRepository = new DynamoDbAuthcodeRepository(
                    dynamoDbClient(),
                    tableName(AuthcodeRepository.class)
            );
            if (autocreateTables) authcodeRepository.createTableIfNotExists();
        }
        return authcodeRepository;
    }

    @Override
    public DynamoDbUserRepository userRepository() {
        if (userRepository == null) {
            userRepository = new DynamoDbUserRepository(
                    dynamoDbClient(),
                    tableName(UserRepository.class),
                    acceptingSignup
            );
            if (autocreateTables) userRepository.createTableIfNotExists();
        }
        return userRepository;
    }

    @Override
    public DynamoDbOauthSessionRepository oauthSessionRepository() {
        if (oauthSessionRepository == null) {
            oauthSessionRepository = new DynamoDbOauthSessionRepository(
                    dynamoDbClient(),
                    tableName(OauthSessionRepository.class)
            );
            if (autocreateTables) oauthSessionRepository.createTableIfNotExists();
        }
        return oauthSessionRepository;
    }
}
