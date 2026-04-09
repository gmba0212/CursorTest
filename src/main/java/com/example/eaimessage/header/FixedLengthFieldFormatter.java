package com.example.eaimessage.header;

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

    public static String padRight(String value, int length) {
        return rightPad(value, length);
    }

    public static String zeroPadNumber(int value, int length) {
        int safe = Math.max(value, 0);
        String formatted = String.format("%0" + length + "d", safe);
        if (formatted.length() > length) {
            return formatted.substring(formatted.length() - length);
        }
        return formatted;
    }
}
