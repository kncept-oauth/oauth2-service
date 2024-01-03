package com.kncept.oauth2.config.parameter;

import com.kncept.oauth2.config.SimpleCrudRepository;

public enum ConfigParameters {

    requirePkce("false"),
    signupEnabled("true"),
    rootUri,
    sessionDuration("3600"), // seconds
//    issuerName("kncept-oauth"), // TODO: rename to 'issuer' and MUST be a URL... might need to scrape from web requests
    ;

    private final String defaultValue;

    ConfigParameters(){
        defaultValue = "";
    }
    ConfigParameters(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String get(SimpleCrudRepository<Parameter> repository) {
        Parameter param = repository.read(Parameter.id(name()));
        if (param == null || param.getValue() == null)
            return defaultValue;
        return param.getValue();
    }

}
