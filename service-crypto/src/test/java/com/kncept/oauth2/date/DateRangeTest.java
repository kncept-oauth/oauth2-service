package com.kncept.oauth2.date;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeTest {

    @Test
    public void infiniteDateRangeIsValid() {
        assertNotNull(DateRange.infinite);
    }

    @Test
    public void overlapsWork() {
        long epochNow = System.currentTimeMillis() / 1000;
        DateRange now = new DateRange(epochNow, epochNow + 60);

        assertFalse(now.contains(Instant.ofEpochSecond(epochNow - 1)));

        assertTrue(now.contains(Instant.ofEpochSecond(epochNow + 0)));
        assertTrue(now.contains(Instant.ofEpochSecond(epochNow + 30)));
        assertTrue(now.contains(Instant.ofEpochSecond(epochNow + 59)));

        assertFalse(now.contains(Instant.ofEpochSecond(epochNow + 60)));
        assertFalse(now.contains(Instant.ofEpochSecond(epochNow + 61)));
    }

}