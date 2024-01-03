package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authcode.Authcode;
import com.kncept.oauth2.config.authrequest.AuthRequest;
import com.kncept.oauth2.config.client.Client;
import com.kncept.oauth2.config.crypto.ExpiringKeypair;
import com.kncept.oauth2.config.parameter.Parameter;
import com.kncept.oauth2.config.session.OauthSession;
import com.kncept.oauth2.config.user.User;
import com.kncept.oauth2.config.user.UserLogin;
import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.*;

public class SingleStorageConfiguration implements Oauth2StorageConfiguration {
    final CrudRepo repo;
    private final  Map<Class<?>, SimpleCrudRepository<? extends IdentifiedEntity>> repositories;
    public SingleStorageConfiguration(CrudRepo repo) {
        this.repo = repo;
        repositories = new HashMap<>();
    }

    private <T extends IdentifiedEntity> SimpleCrudRepository<T> lookupSimpleCrudRepo(Class<T> javaType, String... entityTypes) {
        return (SimpleCrudRepository<T>) repositories.computeIfAbsent(javaType, (key) -> {
            for(String entityType: entityTypes) repo.registerEntityType(entityType, javaType);
            return new SimpleCrudRepository<T>() {
                private void validateId(EntityId id) {
                    id.validate(entityTypes);
                }
                @Override
                public void create(T entity) {
                    validateId(entity.getId());
                    repo.create(entity);
                }

                @Override
                public T read(EntityId id) {
                    validateId(id);
                    return repo.read(id);
                }

                @Override
                public void update(T entity) {
                    validateId(entity.getId());
                    repo.update(entity);
                }

                @Override
                public void delete(T entity) {
                    validateId(entity.getId());
                    repo.update(entity);
                }

                @Override
                public List<T> list() {
                    return repo.list(entityTypes);
                }
            };
        });
    }

    @Override
    public SimpleCrudRepository<Client> clientRepository() {
        return lookupSimpleCrudRepo(Client.class, Client.EntityType);
    }

    @Override
    public SimpleCrudRepository<AuthRequest> authRequestRepository() {
        return lookupSimpleCrudRepo(AuthRequest.class, AuthRequest.EntityType);
    }

    @Override
    public SimpleCrudRepository<Authcode> authcodeRepository() {
        return lookupSimpleCrudRepo(Authcode.class, Authcode.EntityType);
    }

    @Override
    public SimpleCrudRepository<User> userRepository() {
        return lookupSimpleCrudRepo(User.class, User.EntityType);
    }

    @Override
    public SimpleCrudRepository<UserLogin> userLoginRepository() {
        return lookupSimpleCrudRepo(UserLogin.class, stream(UserLogin.UserLoginType.values()).map(Enum::name).toList().toArray(new String[0]));
    }

    @Override
    public SimpleCrudRepository<OauthSession> oauthSessionRepository() {
        return lookupSimpleCrudRepo(OauthSession.class, OauthSession.EntityType);
    }

    @Override
    public SimpleCrudRepository<Parameter> parameterRepository() {

        return lookupSimpleCrudRepo(Parameter.class, Parameter.EntityType);
    }

    @Override
    public SimpleCrudRepository<ExpiringKeypair> expiringKeypairRepository() {
        return lookupSimpleCrudRepo(ExpiringKeypair.class, ExpiringKeypair.EntityType);
    }


    public interface CrudRepo {

        <T extends IdentifiedEntity> void registerEntityType(String entityType, Class<T> javaType);

        <T extends IdentifiedEntity> void create(T entity);
        <T extends IdentifiedEntity> T read(EntityId id);
        <T extends IdentifiedEntity> List<T> list(String... entityTypes);
        <T extends IdentifiedEntity> void update(T entity);

        <T extends IdentifiedEntity> void delete(T entity);
    }

}
