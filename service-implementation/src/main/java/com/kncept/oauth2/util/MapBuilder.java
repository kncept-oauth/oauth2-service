package com.kncept.oauth2.util;

import java.util.Map;
import java.util.TreeMap;

public class MapBuilder<K, V> {

    private final Map<K, V> data = new TreeMap<>();

    public MapBuilder<K, V> with(K key, V value) {
        data.put(key, value);
        return this;
    }

    public Map<K, V> get() {
        return data;
    }
}
