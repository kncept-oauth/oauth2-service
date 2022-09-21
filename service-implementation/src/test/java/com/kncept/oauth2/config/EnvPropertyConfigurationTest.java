package com.kncept.oauth2.config;

import com.kncept.oauth2.config.crypto.ExpiringKeypairRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvPropertyConfigurationTest {

    EnvPropertyConfiguration config = new EnvPropertyConfiguration();

    @Test
    public void canDeterminePropertySuffix() {
        assertEquals("ExpiringKeypair", config.propertySuffixFromInterface(ExpiringKeypairRepository.class));
    }

    @Test
    public void defaultValueIsUsedWhenEnvPropertyIsNotDefined() {
        Object defaultObj = new Object();
        Object loaded = config.loadClassFromEnvProperty("non existent", () -> defaultObj);
        assertEquals(defaultObj, loaded);
    }

}