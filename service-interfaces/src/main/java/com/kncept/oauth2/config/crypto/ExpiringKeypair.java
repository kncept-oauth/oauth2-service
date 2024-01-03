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


    EntityId id;
    String privateKey;
    String publicKey;
    LocalDateTime validFrom;
    LocalDateTime validTo;
    LocalDateTime expiry;

    @Override
    public IdentifiedEntity clone() {
        try {
            return (IdentifiedEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
