package com.kncept.oauth2.config.crypto;


import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ExpiringKeypair implements IdentifiedEntity {

    public static final String EntityType = "keypair";

    public static EntityId id(String value) {
        return EntityId.parse(EntityType, value);
    }

    // TODO: unique keys per client?
    // this would need a way to differenciate clients for the jwks request though ;/
    EntityId id;
    String privateKey;
    String publicKey;
    LocalDateTime when; // valid from
    LocalDateTime expiry; // valid to

    @Override
    public EntityId getRef() {
        return id;
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
        if (privateKey == null) throw new IllegalStateException();
        if (publicKey == null) throw new IllegalStateException();
        if (when == null) throw new IllegalStateException();
        if (expiry == null) throw new IllegalStateException();
    }
}
