package com.kncept.oauth2.config.parameter;

public class SimpleParameter implements Parameter {

    private final String name;
    private final String value;

    public SimpleParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return this.value;
    }
}
