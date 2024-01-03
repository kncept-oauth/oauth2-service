package com.kncept.oauth2.config.authcode;


import com.kncept.oauth2.config.user.User;
import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class Authcode implements IdentifiedEntity {

    public static final String EntityType = "auth-code";
    public static final String RefType = User.EntityType;

    public static EntityId id(String value) {
        return EntityId.parse(EntityType, value);
    }

    private EntityId id;
    private EntityId ref;
    private String oauthSessionId;
    private LocalDateTime expiry;

    @Override
    public LocalDateTime getWhen() {
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

    @Override
    public void validate() {
        if (oauthSessionId == null) throw new IllegalStateException();
        if (expiry == null) throw new IllegalStateException();
    }
}
