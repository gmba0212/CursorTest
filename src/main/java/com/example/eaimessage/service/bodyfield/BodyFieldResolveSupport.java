package com.example.eaimessage.service.bodyfield;

import com.example.eaimessage.model.MessageContext;

public final class BodyFieldResolveSupport {

    private BodyFieldResolveSupport() {
    }

    public static String valueOr(MessageContext data, String key, String defaultValue) {
        String v = data.getString(key);
        return v == null || v.isBlank() ? defaultValue : v;
    }

    public static String defaultString(String value) {
        return value == null ? "" : value;
    }

    public static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return "";
    }
}
