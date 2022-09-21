package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authrequest.AuthRequestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DynoDbOauth2ConfigurationTest {

    @Test
    public void generatesExpectedDefaultTablenames() {
        DynoDbOauth2Configuration configuration = new DynoDbOauth2Configuration(false);
        String generatedTableName = configuration.tableName(AuthRequestRepository.class);
        Assertions.assertEquals("KnceptOidcAuthRequestRepository", generatedTableName);
    }
}
