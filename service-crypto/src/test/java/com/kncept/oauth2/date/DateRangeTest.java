package com.kncept.oauth2.date;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

        assertFalse(now.contains(ofEpochSecond(epochNow - 1)));

        assertTrue(now.contains(ofEpochSecond(epochNow + 0)));
        assertTrue(now.contains(ofEpochSecond(epochNow + 30)));
        assertTrue(now.contains(ofEpochSecond(epochNow + 59)));

        assertFalse(now.contains(ofEpochSecond(epochNow + 60)));
        assertFalse(now.contains(ofEpochSecond(epochNow + 61)));
    }

    private LocalDateTime ofEpochSecond(long epoch) {
        return LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC);
    }
}