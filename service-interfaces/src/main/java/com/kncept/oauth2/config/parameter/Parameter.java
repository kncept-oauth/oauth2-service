package com.kncept.oauth2.config.parameter;

import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Parameter implements IdentifiedEntity {

    public static final String EntityType = "param";

    public static EntityId id(String name) {
        return EntityId.parse(EntityType, name);
    }

    EntityId id;
    String value;

    @Override
    public LocalDateTime getExpiry() {
        return null;
    }

    @Override
    public IdentifiedEntity clone() {
        try {
            return (IdentifiedEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
