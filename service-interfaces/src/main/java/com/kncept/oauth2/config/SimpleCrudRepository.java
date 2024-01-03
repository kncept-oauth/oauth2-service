package com.kncept.oauth2.config;

import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;

import java.util.List;

public interface SimpleCrudRepository<T extends IdentifiedEntity> {
    void create(T entity);
    T read(EntityId id);
    void update (T entity);
    void delete (T entity);

    //how do I do a list?
    List<T> list();
}
