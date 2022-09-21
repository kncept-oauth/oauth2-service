package com.kncept.oauth2.date;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateRange {

    public static long epochSecond() {
        return System.currentTimeMillis() / 1000;
    }

    private static LocalDateTime min = LocalDateTime.ofEpochSecond(Long.MIN_VALUE, 0, ZoneOffset.UTC);
    private static LocalDateTime max = LocalDateTime.ofEpochSecond(Long.MAX_VALUE, 0, ZoneOffset.UTC);

    public static DateRange infinite = new DateRange(true, true, LocalDateTime.MIN, LocalDateTime.MAX);

    private final boolean includesStart;
    private final boolean includesEnd;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public DateRange(
            LocalDateTime start,
            LocalDateTime end
    ) {
        this(true, false, start, end);
    }

    public DateRange(
            boolean includesStart,
            boolean includesEnd,
            LocalDateTime start,
            LocalDateTime end
    ) {
        this.includesStart = includesStart;
        this.includesEnd = includesEnd;
        this.start = start;
        this.end = end;
    }

    public DateRange(
            long start,
            long end
    ) {
        includesStart = true;
        includesEnd = false;
        this.start = LocalDateTime.ofEpochSecond(start, 0, ZoneOffset.UTC);
        this.end = LocalDateTime.ofEpochSecond(end, 0, ZoneOffset.UTC);
    }

    public boolean contains(LocalDateTime when) {
        if (when.isBefore(start)) return false;
        if (includesStart && when.isEqual(start)) return true;
        if (when.isAfter(end)) return false;
        if (includesEnd && when.isEqual(end)) return true;
        return true;
    }

    public LocalDateTime start() {
        return start;
    }

    public LocalDateTime end() {
        return end;
    }

    @Override
    public String toString() {
        return
                (includesStart ? "[" : "(") +
                start +
                "," +
                end +
                (includesEnd ? "]" : ")");
    }
}
