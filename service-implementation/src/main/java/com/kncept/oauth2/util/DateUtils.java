package com.kncept.oauth2.util;

import java.time.Clock;
import java.time.LocalDateTime;

public class DateUtils {

    public static LocalDateTime utcNow() {
        return LocalDateTime.now(Clock.systemUTC());
    }
}
