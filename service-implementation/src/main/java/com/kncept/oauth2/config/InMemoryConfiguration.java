package com.kncept.oauth2.config;

import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryConfiguration extends SingleStorageConfiguration {


    public InMemoryConfiguration(){
        super(new InMemoryCrudRepo());
    }

    public Map<EntityId, IdentifiedEntity> repo() {
        return ((InMemoryCrudRepo)super.repo).repo;
    }

    private static class InMemoryCrudRepo implements SingleStorageConfiguration.CrudRepo {
        public final Map<EntityId, IdentifiedEntity> repo = new HashMap<>();

        @Override
        public <T extends IdentifiedEntity> void registerEntityType(String entityType, Class<T> javaType) {
            // meh. irrelevant here (!!)
        }

        public <T extends IdentifiedEntity> T read(EntityId id) {
            IdentifiedEntity entity = repo.get(id);
            if (entity != null) return (T) entity.clone();
            return null;
        }
        public <T extends IdentifiedEntity> void create(T entity) {
            if(repo.containsKey(entity.getId())) throw new IllegalStateException("Already Exists: " + entity.getId());
            repo.put(entity.getId(), entity);
        }
        public <T extends IdentifiedEntity> void update(T entity) {
            if(!repo.containsKey(entity.getId())) throw new IllegalStateException("Doesn't Exist: " + entity.getId());
            repo.put(entity.getId(), entity);
        }

        public <T extends IdentifiedEntity> void delete(T entity) {
            if(!repo.containsKey(entity.getId())) throw new IllegalStateException("Doesn't Exist: " + entity.getId());
            repo.remove(entity.getId());
        }

        @Override
        public <T extends IdentifiedEntity> List<T> list(String... entityTypes) {
            return (List<T>) repo.values().stream().filter(v -> v.getId().isOfType(entityTypes)).collect(Collectors.toList());
        }
    }
}

