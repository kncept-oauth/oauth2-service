package com.kncept.oauth2.config.parameter;

public class EmptyParameterRepository implements ParameterRepository {
    @Override
    public Parameter parameter(String name) {
        return null;
    }
}
