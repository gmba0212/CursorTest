package com.example.eaimessage.builder;

public final class FixedLengthFieldFormatter {

    private FixedLengthFieldFormatter() {
    }

    public static String rightPad(String value, int length) {
        String safe = value == null ? "" : value;
        if (safe.length() > length) {
            safe = safe.substring(0, length);
        }
        return String.format("%-" + length + "s", safe);
    }
}
