package com.kncept.oauth2.date;

import java.time.LocalDateTime;

public class DateRange {

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
