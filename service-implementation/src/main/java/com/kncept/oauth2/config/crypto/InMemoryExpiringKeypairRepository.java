package com.kncept.oauth2.config.crypto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryExpiringKeypairRepository implements ExpiringKeypairRepository {

    List<ExpiringKeypair> keys = new ArrayList<>();

    void filterExpired() {
        long epochSeconds = System.currentTimeMillis() / 1000;
        for(int i = 0; i < keys.size(); i++) {
            ExpiringKeypair key = keys.get(i);
            if (key.deletionTime() < epochSeconds) {
                keys.remove(i);
                i--;
            }
        }
    }

    @Override
    public Optional<ExpiringKeypair> lookup(String id) {
        filterExpired();
        return keys.stream().filter(k -> id.equals(k.id())).findAny();
    }

    @Override
    public Optional<ExpiringKeypair> latest() {
        filterExpired();
        long epochSeconds = System.currentTimeMillis() / 1000;
        return keys.stream().filter(k -> k.validFrom() < epochSeconds && k.validTo() > epochSeconds).findAny();
    }

    @Override
    public void save(ExpiringKeypair kp) {
        keys.add(kp);
    }

    @Override
    public List<ExpiringKeypair> all() {
        filterExpired();
        return new ArrayList<>(keys);
    }
}
