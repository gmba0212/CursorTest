package com.example.eaimessage.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FixedLengthFieldFormatter {

    private static final Logger log = LoggerFactory.getLogger(FixedLengthFieldFormatter.class);

    private FixedLengthFieldFormatter() {
    }

    public static String rightPad(String value, int length) {
        String safe = value == null ? "" : value;
        if (safe.length() > length) {
            log.debug("고정 길이 필드 잘림 targetLength={}, originalLength={}", length, safe.length());
            safe = safe.substring(0, length);
        }
        return String.format("%-" + length + "s", safe);
    }
}
