package com.kncept.oauth2.config.client;

public class SimpleClient implements Client {
    private final String clientId;
    private boolean enabled;

    public SimpleClient(String clientId) {
        this(clientId, true);
    }
    public SimpleClient(String clientId, boolean enabled) {
        this.clientId = clientId;
        this.enabled = enabled;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    @Override
    public String clientId() {
        return clientId;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

}
