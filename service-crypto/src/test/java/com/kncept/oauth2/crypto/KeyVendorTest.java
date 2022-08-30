package com.kncept.oauth2.crypto;

import com.kncept.oauth2.date.DateRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class KeyVendorTest {

    @Test
    public void startOfDayIsWindowZero() {
        KeyVendor kv = new KeyVendor();
        int windowNumber = kv.windowNumber(LocalDate.now().atStartOfDay());
        Assertions.assertEquals(0, windowNumber);
    }

    @Test
    public void lessThanSixHoursIsWindowZero() {
        KeyVendor kv = new KeyVendor();
        LocalDateTime sod = LocalDate.now().atStartOfDay();
        LocalDateTime lastSecond = sod.plusHours(6).minusSeconds(1);
        int windowNumber = kv.windowNumber(lastSecond);
        Assertions.assertEquals(0, windowNumber);
    }

    @Test
    public void sixHoursIsWindowOne() {
        KeyVendor kv = new KeyVendor();
        LocalDateTime sod = LocalDate.now().atStartOfDay();
        LocalDateTime time = sod.plusHours(6);
        int windowNumber = kv.windowNumber(time);
        Assertions.assertEquals(1, windowNumber);
    }

    @Test
    public void sixHoursAndOneSecondIsWindowOne() {
        KeyVendor kv = new KeyVendor();
        LocalDateTime sod = LocalDate.now().atStartOfDay();
        LocalDateTime time = sod.plusHours(6).plusSeconds(1);
        int windowNumber = kv.windowNumber(time);
        Assertions.assertEquals(1, windowNumber);
    }

    @Test
    public void windowsPerDay() {
        Assertions.assertEquals(4, new KeyVendor().windowsPerDay());
    }

    @Test
    public void canGeneratedateRangeForInitialWindow() {
        DateRange range = new KeyVendor().dateRangeForWindow(LocalDate.now(), 0);
        Assertions.assertEquals(range.start().getHour(), 0);
        // Initial windows should include SOD
        Assertions.assertTrue(range.contains(LocalDate.now().atStartOfDay()));
    }

    @Test
    public void canGeneratedateRangeForFirstWindow() {
        DateRange range = new KeyVendor().dateRangeForWindow(LocalDate.now(), 1);
        Assertions.assertEquals(range.start().getHour(), 6);
    }
}
