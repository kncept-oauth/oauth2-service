package com.kncept.oauth2.configuration;

import com.kncept.oauth2.config.SystemProperyConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SystemProperyConfigurationTest {

    @Test
    public void returnsNullOnEmptySystemProperty() {
        Assertions.assertThrows(NullPointerException.class, () -> SystemProperyConfiguration.loadClassFromSystemProperty("none", Object.class));
    }

}
