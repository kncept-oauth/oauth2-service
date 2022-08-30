package com.kncept.oauth2.html;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleHtmlInflaterTest {

    private SimpleHtmlInflater inflater = new SimpleHtmlInflater();

    @Test
    public void htmlPageResourceHasToExist() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> inflater.loadHtmlPageResource("/html/none.html"));
    }

    @Test
    public void canFindHtmlPageResources() {
        inflater.loadHtmlPageResource("/html/login.html");
    }

    @Test
    public void canReplaceDollarCurlyTokens() {
        String value = inflater.replace("xx ${key} xx", SimpleHtmlInflater.dollarCurly, v -> "__" + v.toUpperCase() + "__");
        Assertions.assertEquals("xx __KEY__ xx", value);
    }

    @Test
    public void canReplaceHashCurlyTokens() {
        String value = inflater.replace("xx #{key} xx", SimpleHtmlInflater.hashCurly, v -> "__" + v.toUpperCase() + "__");
        Assertions.assertEquals("xx __KEY__ xx", value);
    }


}
