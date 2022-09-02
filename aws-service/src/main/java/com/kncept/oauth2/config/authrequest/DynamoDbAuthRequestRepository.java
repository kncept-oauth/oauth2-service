package com.kncept.oauth2.config.authrequest;

import com.kncept.oauth2.config.DynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;

public class DynamoDbAuthRequestRepository extends DynamoDbRepository<AuthRequest> implements AuthRequestRepository {

    public DynamoDbAuthRequestRepository (DynamoDbClient client, String tableName) {
        super(
                AuthRequest.class,
                client,
                tableName
        );
    }

    @Override
    public AuthRequest createAuthRequest(String oauthSessionId, Optional<String> state, Optional<String> nonce, String redirectUri, String clientId, String responseType) {
        SimpleAuthRequest authReqeust = new SimpleAuthRequest(
                oauthSessionId,
                state,
                nonce,
                redirectUri,
                clientId,
                responseType
        );
        write(oauthSessionId, authReqeust);
        return authReqeust;
    }

    @Override
    public Optional<AuthRequest> lookup(String oauthSessionId) {
        return Optional.ofNullable(findById(oauthSessionId));
    }
}
