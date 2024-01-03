package com.kncept.oauth2.crypto.key;

import com.kncept.oauth2.config.crypto.ExpiringKeypair;
import com.kncept.oauth2.date.DateRange;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

class ManagedKeypairConverterTest {

    @Test
    public void canConvertInfiniteDateRange() {
        KeyGenerator gen = new KeyGenerator();


        ManagedKeypair kp = new ManagedKeypair(
                ExpiringKeypair.id("id"),
                gen.generateKeypair(),
                DateRange.infinite
        );
        ExpiringKeypair ekp = ManagedKeypairConverter.convert(kp);
        assertNotNull(ekp);
    }

}