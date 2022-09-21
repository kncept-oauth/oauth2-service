package com.kncept.oauth2.date;

import java.time.Instant;

public class DateRange {
    public static long epochSecond() {
        return System.currentTimeMillis() / 1000;
    }

    private static Instant min = Instant.MIN; //Instant.ofEpochSecond(Long.MIN_VALUE);
    private static Instant max = Instant.MAX; //Instant.ofEpochSecond(Long.MAX_VALUE);

    public static DateRange infinite = new DateRange(true, true, min, max);

    private final boolean includesStart;
    private final boolean includesEnd;
    private final Instant start;
    private final Instant end;

    public DateRange(
            Instant start,
            Instant end
    ) {
        this(true, false, start, end);
    }

    public DateRange(
            boolean includesStart,
            boolean includesEnd,
            Instant start,
            Instant end
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
        this.start = Instant.ofEpochSecond(start);
        this.end = Instant.ofEpochSecond(end);
    }

    public boolean contains(Instant when) {
        if (when.isBefore(start)) return false;
        if (when.isAfter(end)) return false;
        if (when.compareTo(start) == 0) return includesStart;
        if (when.compareTo(end) == 0) return includesEnd;
        return true;
    }

    public Instant start() {
        return start;
    }

    public Instant end() {
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
