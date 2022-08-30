package com.kncept.oauth2.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Oauth2ConfigurationTest {


    @Test
    public void returnsNullOnEmptySystemProperty() {
        Object obj = Oauth2Configuration.loadClassFromSystemProperty("none", Object.class);
        Assertions.assertNull(obj);
    }

}
