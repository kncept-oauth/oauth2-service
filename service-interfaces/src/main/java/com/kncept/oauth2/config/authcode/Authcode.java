package com.kncept.oauth2.config.authcode;


import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Authcode implements IdentifiedEntity {

    public static final String EntityType = "auth-code";

    public static EntityId id(String value) {
        return EntityId.parse(EntityType, value);
    }

    private EntityId id;
    private String oauthSessionId;
    private LocalDateTime expiry;

    @Override
    public IdentifiedEntity clone() {
        try {
            return (IdentifiedEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
