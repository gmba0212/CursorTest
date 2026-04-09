package com.example.eaimessage.renderer;

abstract class AbstractChannelRendererSupport {

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
