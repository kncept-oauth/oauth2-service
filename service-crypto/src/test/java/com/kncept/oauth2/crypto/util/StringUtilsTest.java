package com.kncept.oauth2.crypto.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.kncept.oauth2.crypto.util.StringUtils.*;

class StringUtilsTest {

    @Test
    public void wontSplitAShortString() {
        String s = "short";
        String split = splitToMultiLine(s, 32);
        assertEquals(s, split);
    }

    @Test
    public void stringsCanBeSplit() {
        String s = "abcde";
        String split = splitToMultiLine(s, 2);
        assertEquals("ab\ncd\ne", split);
    }

}