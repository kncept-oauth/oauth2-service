package com.kncept.oauth2.crypto.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static String splitToMultiLine(String input, int lineLength) {
        List<String> out = new ArrayList<>();
        while(!"".equals(input)) {
            int endIndex = Math.min(lineLength, input.length());
            out.add(input.substring(0, endIndex));
            input = input.substring(endIndex);
        }
        return out.stream().reduce((s0, s1) -> s0 + "\n" + s1).orElse("");
    }

}
