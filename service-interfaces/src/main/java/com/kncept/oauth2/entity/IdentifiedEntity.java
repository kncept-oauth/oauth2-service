package com.kncept.oauth2.entity;

import java.time.LocalDateTime;

public interface IdentifiedEntity extends Cloneable {

    public EntityId getId();

    // nullable
    public LocalDateTime getExpiry();

    public Object clone();
}
