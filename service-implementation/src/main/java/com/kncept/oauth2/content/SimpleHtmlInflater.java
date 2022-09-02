package com.kncept.oauth2.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleHtmlInflater {

    public static final Pattern dollarCurly = Pattern.compile("\\$\\{(.+?)\\}");
    public static final Pattern hashCurly = Pattern.compile("\\#\\{(.+?)\\}");

    public String loadHtmlPageResource(String resourceLocation) {
        try {
            InputStream in = SimpleHtmlInflater.class.getResourceAsStream(resourceLocation);
            String text = new String(in.readAllBytes());
            return text;
        } catch (IOException e) {
            throw new RuntimeException("Unable to load html page " + resourceLocation, e);
        }
    }

    public String inflate(String html, Map<String, String> params) {
        html = replace(html, dollarCurly, params::get);
        html = replace(html, hashCurly, System::getProperty);
        return html;
    }

    // https://stackoverflow.com/questions/959731/how-to-replace-a-set-of-tokens-in-a-java-string
    public String replace(String text, Pattern pattern, Function<String, String> mapper) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = mapper.apply(matcher.group(1));
            builder.append(text.substring(i, matcher.start()));
            if (replacement != null) // filter nulls as empty
                builder.append(replacement);
            i = matcher.end();
        }
        builder.append(text.substring(i));
        return builder.toString();
    }

}
