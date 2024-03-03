package com.kncept.oauth2.config.authrequest;

import com.kncept.oauth2.config.client.Client;
import com.kncept.oauth2.config.user.User;
import com.kncept.oauth2.entity.EntityId;
import com.kncept.oauth2.entity.IdentifiedEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Arrays.asList;

@Data
public class AuthRequest implements IdentifiedEntity {

    public static final String EntityType = "auth-request";
    public static final String RefType = Client.EntityType;

    public static EntityId id(String value) {
        return EntityId.parse(EntityType, value);
    }

    private EntityId id;
    private EntityId ref; // CLIENT id
    private Optional<String> state;
    private Optional<String> nonce;
    private String redirectUri;
    private String responseType;
    private LocalDateTime expiry;

    private EntityId userId; // nullable
    private EntityId oauthSessionId; // nullable... WHY do we even need this?

    ////////// Optionals don't add enough, and they obscure types
    private boolean pkce;
    private String pkceCodeChallenge;
    private String pkceChallengeType;


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
        if (redirectUri == null) throw new IllegalStateException();
        if (responseType == null) throw new IllegalStateException();
        if (expiry == null) throw new IllegalStateException();
        if(pkce) {
            if (pkceCodeChallenge == null) throw new IllegalStateException();
            if (pkceChallengeType == null) throw new IllegalStateException();
        }
        if (userId != null) userId.validate(User.EntityType);
    }
}
