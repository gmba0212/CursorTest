package com.example.eaimessage.builder.content;

import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;

abstract class AbstractMessageContentSupport {

    protected String param(TalkRequest request, String key, String defaultValue) {
        if (request.getParams() == null) {
            return defaultValue;
        }
        Object value = request.getParams().get(key);
        if (value == null) {
            return defaultValue;
        }
        String s = String.valueOf(value);
        return s.isBlank() ? defaultValue : s;
    }

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
