package com.kncept.oauth2.crypto.key.strategy;

import com.kncept.oauth2.crypto.key.ManagedKeypair;

public interface KeypairStrategy {

    public ManagedKeypair current();

}
