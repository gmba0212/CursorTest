package com.example.eaimessage.builder.content;

import com.example.eaimessage.model.ServiceData;

abstract class AbstractMessageContentSupport {

    protected String valueOr(ServiceData data, String key, String defaultValue) {
        String v = data.getString(key);
        return v == null || v.isBlank() ? defaultValue : v;
    }

    protected String defaultString(String value) {
        return value == null ? "" : value;
    }

    protected String firstNonBlank(String... values) {
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
