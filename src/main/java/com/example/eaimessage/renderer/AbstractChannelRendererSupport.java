package com.example.eaimessage.renderer;

import com.example.eaimessage.model.TalkRequest;

abstract class AbstractChannelRendererSupport {

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
