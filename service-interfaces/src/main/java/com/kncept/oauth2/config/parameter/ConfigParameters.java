package com.kncept.oauth2.config.parameter;

public enum ConfigParameters {

    requirePkce("false"),
    signupEnabled("true"),
    rootUri,
    sessionDuration("3600"), // seconds
    issuerName("kncept-oauth"),
    ;

    private String defaultValue = "";

    ConfigParameters(){}
    ConfigParameters(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String get(ParameterRepository repository) {
        Parameter param = repository.getParameter(name());
        if (param == null || param.value() == null)
            return defaultValue;
        return param.value();
    }

}
