package com.kncept.oauth2.config.session;

import com.kncept.oauth2.config.DynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;
import java.util.UUID;

public class DynamoDbOauthSessionRepository extends DynamoDbRepository<OauthSession> implements OauthSessionRepository {

    public DynamoDbOauthSessionRepository(DynamoDbClient client, String tableName) {
        super(
                OauthSession.class,
                client,
                tableName
        );
    }

    @Override
    public OauthSession createSession() {
        SimpleOauthSession session = new SimpleOauthSession(UUID.randomUUID().toString(), Optional.empty());
        write(session.oauthSessionId(), session);
        return session;
    }

    @Override
    public Optional<OauthSession> lookup(String oauthSessionId) {
        return Optional.ofNullable(findById(oauthSessionId));
    }

    @Override
    public Optional<OauthSession> authenticate(String oauthSessionId, String userId) {
        OauthSession session = findById(oauthSessionId);
        if (session != null) {
            session = new SimpleOauthSession(oauthSessionId, Optional.of(userId));
            write(oauthSessionId, session);
        }
        return Optional.ofNullable(session);
    }
}
