package com.kncept.oauth2.config.user;

import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserLogin implements IdentifiedEntity {
    public enum UserLoginType {
        email, // email, require 2fa
        phone, // phone require 2fa
        ;

        public boolean isValid(String value) {
            switch (this) {
                case email -> {
                    return value.contains("@");
                }
            }
                return false;
        }
        public String normalize(String value) {
            return value.trim();
        }
    }
    public static EntityId id(UserLoginType type, String value) {
        return EntityId.parse(type.toString(), value);
    }

    EntityId id;
    EntityId ref;

    boolean verified = false;

    String tokenValue;
    LocalDateTime tokenExpires;
    LocalDateTime tokenIssued;

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

    @Override
    public LocalDateTime getWhen() {
        return null;
    }

    @Override
    public void validate() {}

}
