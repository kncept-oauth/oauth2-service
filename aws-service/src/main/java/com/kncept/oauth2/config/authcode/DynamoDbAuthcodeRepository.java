package com.kncept.oauth2.config.authcode;

import com.kncept.oauth2.config.DynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;

public class DynamoDbAuthcodeRepository extends DynamoDbRepository<Authcode> implements AuthcodeRepository {

    public DynamoDbAuthcodeRepository (DynamoDbClient client, String tableName) {
        super(
                Authcode.class,
                client,
                tableName
        );
    }

    @Override
    public Authcode create(String authCode, String oauthSessionId) {
        Authcode code = new SimpleAuthcode(authCode, oauthSessionId);
        write(authCode, code);
        return code;
    }

    @Override
    public Optional<Authcode> lookup(String authCode) {
        return Optional.ofNullable(findById(authCode));
    }
}
