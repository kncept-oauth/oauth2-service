package com.kncept.oauth2.crypto.key;

import com.kncept.oauth2.date.DateRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

// This really needs to be optimised, and tightened down
public class KeyVendor {

    KeyGenerator keyGenerator = new KeyGenerator();

    private int numberOfOldWindowsToKeep = 2;
    private List<ExpiringKeyPair> keys = new CopyOnWriteArrayList<>();

    public long millisKeyDuration; // longer total expiry duration
    public long millisKeyWindow; // shorter key reissue time

    public KeyVendor() {
        millisKeyDuration = TimeUnit.HOURS.toMillis(18);
        millisKeyWindow = TimeUnit.HOURS.toMillis(6);
    }

    public KeyVendor(
            long millisKeyDuration,
            long millisKeyWindow
    ) {
        this.millisKeyDuration = millisKeyDuration;
        this.millisKeyWindow = millisKeyWindow;
    }

    public ExpiringKeyPair getPair() {
        return getPair(LocalDateTime.now());
    }

    public ExpiringKeyPair getPair(LocalDateTime when) {
        LocalDateTime now = LocalDateTime.now();
        if (when.isAfter(now)) throw new RuntimeException("Future Dates not supported"); // yet ?


        // add any extra dates if required...
        if (keys.isEmpty() || keys.get(0).validity().contains(now)) {
            keys.add(generateCurrent(now));
        }
        // keep two days worth of keys?
        while(keys.size() > (2 * windowsPerDay())) keys.remove(keys.size() - 1);

        int currentWindow = windowNumber(now);

        // only for +/- one day, as well.
        LocalDateTime sod = LocalDate.now().atStartOfDay();

        // is this today?
        if (when.isAfter(sod) || when.isEqual(sod)) {
            int window = windowNumber(when);
            int windowsAgo = currentWindow - window;
            if (windowsAgo >= keys.size()) throw new RuntimeException("Date is too long ago");
            return keys.get(windowsAgo);
        }

        LocalDateTime yesterdaySod = sod.minusDays(1);
        if (when.isAfter(yesterdaySod) || when.isEqual(yesterdaySod)) {
            int window = windowNumber(when) - windowsPerDay();
            int windowsAgo = currentWindow - window;
            if (windowsAgo >= keys.size()) throw new RuntimeException("Date is too long ago");
            return keys.get(windowsAgo);
        }
        throw new RuntimeException("Date is too old to get a pair from");
    }

    public ExpiringKeyPair generateCurrent(LocalDateTime now) {
        int currentWindow = windowNumber(now);
        DateRange range = dateRangeForWindow(now.toLocalDate(), currentWindow);
        return new ExpiringKeyPair(
                keyGenerator.generateKeypair(),
                range
        );
    }

    public DateRange dateRangeForWindow(LocalDate sod, int window) {
        LocalDateTime start = sod.atStartOfDay().plus(window * millisKeyWindow, TimeUnit.MILLISECONDS.toChronoUnit());
        LocalDateTime end = start.plus(millisKeyWindow, TimeUnit.MILLISECONDS.toChronoUnit());
        return new DateRange(start, end);
    }

    public int windowsPerDay() {
        return (int)(TimeUnit.DAYS.toMillis(1) / millisKeyWindow);
    }


    public int windowNumber(LocalDateTime date) {
        LocalDateTime sod = date.toLocalDate().atStartOfDay();
        long millisElapsed = date.toInstant(ZoneOffset.UTC).toEpochMilli() - sod.toInstant(ZoneOffset.UTC).toEpochMilli();
        return (int)(millisElapsed / millisKeyWindow);
    }

}
