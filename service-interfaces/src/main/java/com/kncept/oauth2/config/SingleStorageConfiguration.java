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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.*;

public class SingleStorageConfiguration implements Oauth2StorageConfiguration {
    final CrudRepo repo;
    private final  Map<Class<?>, SimpleCrudRepository<? extends IdentifiedEntity>> repositories;

    static class MultiMapper {

        private Map<String, Map<String, Class<?>>> entityToJavaType = new HashMap<>();
        private Map<Class<?>, Map<String, List<String>>> javaToEntity = new HashMap();

        <T extends IdentifiedEntity> void validate(T entity) {
            if (javaTypeFor(entity.getId().type, entity.getRef().type) != entity.getClass()) {
                throw new RuntimeException("Validation failed for entity " + entity.getId() + " " + entity.getRef());
            }
        }
        <T extends IdentifiedEntity> Class<T> javaTypeFor(String entityType, String subType) {
            if (entityType == null) throw new NullPointerException();
            if (subType == null) throw new NullPointerException();
            Map<String, Class<?>> entityMap = entityToJavaType.get(entityType);
            Class<?> javaType = entityMap.get(subType);
            if (javaType == null) javaType = entityMap.get("*");
            if (javaType == null) throw new IllegalStateException("Unable to find mapping for " + entityType + " " + subType);
            return (Class<T>) javaType;
        }
        <T extends IdentifiedEntity> List<String> entityTypesFor(Class<T> javaType) {
            Map<String, ?> entityTypes = javaToEntity.get(javaType);
            if (entityTypes == null) throw new IllegalStateException("Unable to find entity type mapping for " + javaType.getName());
            return entityTypes.keySet().stream().toList();
        }

        <T extends IdentifiedEntity> void registerEntityType(String entityType, String subType, Class<T> javaType) {
            if (entityType == null) throw new NullPointerException();
            if (subType == null) subType = "*";

            Map<String, Class<?>> subtypeMapping = entityToJavaType.computeIfAbsent(entityType, key -> new HashMap<>());
            Class<?> existingMapping = subtypeMapping.put(subType, javaType);
            if (existingMapping != null) throw new IllegalStateException("Already registered " + entityType + "-" + subType);

            Map<String, List<String>> entityTypeMapping = javaToEntity.computeIfAbsent(javaType, key -> new HashMap<>());
            List<String> subTypes = entityTypeMapping.computeIfAbsent(entityType, key -> new ArrayList<>());
            if (subTypes.contains(subType)) throw new IllegalStateException("Already registered " + entityType + "-" + subType);
            subTypes.add(subType);
        }

    }

    final MultiMapper multiMapper =  new MultiMapper();



    synchronized <T extends IdentifiedEntity> void registerEntityType(String entityType, String subType, Class<T> javaType) {
        multiMapper.registerEntityType(entityType, subType, javaType);
        repo.registerEntityType(entityType, subType, javaType);
    }

    public SingleStorageConfiguration(CrudRepo repo) {
        this.repo = repo;
        repositories = new HashMap<>();
    }

    private <T extends IdentifiedEntity> SimpleCrudRepository<T> lookupSimpleCrudRepo(Class<T> javaType, Runnable registerAction) {
        return (SimpleCrudRepository<T>) repositories.computeIfAbsent(javaType, (key) -> {
            registerAction.run();
            return new SimpleCrudRepository<T>() {
                private void validateId(EntityId id) {
                    id.validate(multiMapper.entityTypesFor(javaType));
                }
                @Override
                public void create(T entity) {
                    multiMapper.validate(entity);
                    entity.validate();
                    repo.create(entity);
                }

                @Override
                public T read(EntityId id) {
                    validateId(id);
                    return repo.read(id);
                }

                @Override
                public void update(T entity) {
                    multiMapper.validate(entity);
                    entity.validate();
                    repo.update(entity);
                }

                @Override
                public void delete(T entity) {
                    multiMapper.validate(entity);
                    repo.update(entity);
                }

                @Override
                public List<T> list() {
                    return repo.list(multiMapper.entityTypesFor(javaType));
                }
            };
        });
    }

    @Override
    public SimpleCrudRepository<Client> clientRepository() {
        return lookupSimpleCrudRepo(Client.class, () -> {
            registerEntityType(Client.EntityType, Client.EntityType, Client.class);
        });
    }

    @Override
    public SimpleCrudRepository<AuthRequest> authRequestRepository() {
        return lookupSimpleCrudRepo(AuthRequest.class, () -> {
            registerEntityType(AuthRequest.EntityType, Client.EntityType, AuthRequest.class);
        });
    }

    @Override
    public SimpleCrudRepository<Authcode> authcodeRepository() {
        return lookupSimpleCrudRepo(Authcode.class, () -> {
            registerEntityType(Authcode.EntityType, Authcode.RefType, Authcode.class);
        });
    }

    @Override
    public SimpleCrudRepository<User> userRepository() {
        return lookupSimpleCrudRepo(User.class, () -> {
            registerEntityType(User.EntityType, User.EntityType, User.class);
        });
    }

    @Override
    public SimpleCrudRepository<UserLogin> userLoginRepository() {
        return lookupSimpleCrudRepo(UserLogin.class, () -> {
            for(UserLogin.UserLoginType type: UserLogin.UserLoginType.values()) {
                registerEntityType(type.name(), User.EntityType, User.class);
            }
        });
    }

    @Override
    public SimpleCrudRepository<OauthSession> oauthSessionRepository() {
        return lookupSimpleCrudRepo(OauthSession.class, () -> {
//            registerEntityType(OauthSession.EntityType, OauthSession.RefType, OauthSession.class);
//            registerEntityType(OauthSession.EntityType, OauthSession.EntityType, OauthSession.class);
            registerEntityType(OauthSession.EntityType, "*", OauthSession.class);
        });
    }

    @Override
    public SimpleCrudRepository<Parameter> parameterRepository() {
        return lookupSimpleCrudRepo(Parameter.class, () -> {
            registerEntityType(Parameter.EntityType, "*", Parameter.class);
        });
    }

    @Override
    public SimpleCrudRepository<ExpiringKeypair> expiringKeypairRepository() {
        return lookupSimpleCrudRepo(ExpiringKeypair.class, () -> {
            registerEntityType(ExpiringKeypair.EntityType, "*", ExpiringKeypair.class);
        });
    }


    public interface CrudRepo {

        <T extends IdentifiedEntity> void registerEntityType(String entityType, String refType, Class<T> javaType);

        <T extends IdentifiedEntity> void create(T entity);
        <T extends IdentifiedEntity> T read(EntityId id);
        <T extends IdentifiedEntity> List<T> list(List<String> entityTypes);
        <T extends IdentifiedEntity> void update(T entity);

        <T extends IdentifiedEntity> void delete(T entity);
    }

}
