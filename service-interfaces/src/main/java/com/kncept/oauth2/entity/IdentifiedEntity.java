package com.kncept.oauth2.entity;

import java.time.LocalDateTime;

// N.B. there is an inverted index on ref/id called 'gsi' (Global Secondary Index)
public interface IdentifiedEntity extends Cloneable {

    // primary (HASH) key
    public EntityId getId();

    // secondary (RANGE) key
    public EntityId getRef();

    // nullable TTL
    public LocalDateTime getExpiry();

    // nullable secondary index ('when')
    public LocalDateTime getWhen();

    // utility - make cloneable
    public IdentifiedEntity clone();

    // utility - called on create/update
    public void validate();

}
