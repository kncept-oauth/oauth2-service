package com.kncept.oauth2.config.parameter;

public enum ConfigParameters {

    requirePkce("false"),
    signupEnabled("true"),
    rootUri,
    sessionDuration("3600"), // seconds
    issuerName("kncept-oauth"), // TODO: rename to 'issuer' and MUST be a URL... might need to scrape from web requests
    ;

    private final String defaultValue;

    ConfigParameters(){
        defaultValue = "";
    }
    ConfigParameters(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String get(ParameterRepository repository) {
        Parameter param = repository.parameter(name());
        if (param == null || param.value() == null)
            return defaultValue;
        return param.value();
    }

}
