package com.kncept.oauth2.config.session;

import com.kncept.oauth2.config.client.Client;
import com.kncept.oauth2.config.user.User;
import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class OauthSession implements IdentifiedEntity {

    public static final String EntityType = "oauth-session";
    public static final String RefType = User.EntityType;

    public static EntityId id(String value) {
        return EntityId.parse(EntityType, value);
    }

    private EntityId id;
    private EntityId ref;
    private LocalDateTime expiry;

    @Override
    public IdentifiedEntity clone() {
        try {
            return (IdentifiedEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LocalDateTime getWhen() {
        return null;
    }

    @Override
    public void validate() {
    }
}
