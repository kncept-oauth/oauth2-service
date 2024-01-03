package com.kncept.oauth2.config.authrequest;

import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class AuthRequest implements IdentifiedEntity {

    public static final String EntityType = "auth-request";

    public static EntityId id(String value) {
        return EntityId.parse(EntityType, value);
    }

    private EntityId id;
    private Optional<String> state;
    private Optional<String> nonce;
    private String redirectUri;
    private String clientId;
    private String responseType;
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
