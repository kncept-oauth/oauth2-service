package com.kncept.oauth2.config.user;

import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class User implements IdentifiedEntity  {
    public static final String EntityType = "user";

    public static EntityId id() {
        return new EntityId(EntityType, UUID.randomUUID().toString());
    }

    EntityId id;
    String username;

    String salt; // includes encoding about salt/encryption type
    String password;

    LocalDateTime created;

    @Override
    public IdentifiedEntity clone() {
        try {
            return (IdentifiedEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LocalDateTime getExpiry() {
        return null;
    }
}
