package com.kncept.oauth2.config.session;

import com.kncept.oauth2.config.DynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DynamoDbOauthSessionRepository extends DynamoDbRepository<OauthSession> implements OauthSessionRepository {

    long sessionDuration = TimeUnit.SECONDS.toSeconds(300);

    public DynamoDbOauthSessionRepository(DynamoDbClient client, String tableName) {
        super(
                OauthSession.class,
                client,
                tableName
        );
    }


    @Override
    public OauthSession createSession() {
        SimpleOauthSession session = new SimpleOauthSession(
                UUID.randomUUID().toString(),
                Optional.empty(),
                epochSecondsExpiry(sessionDuration));
        write(session);
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
            session = new SimpleOauthSession(
                    oauthSessionId,
                    Optional.of(userId),
                    epochSecondsExpiry(sessionDuration));
            write(session);
        }
        return Optional.ofNullable(session);
    }
}
