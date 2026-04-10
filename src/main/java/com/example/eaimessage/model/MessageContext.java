package com.example.eaimessage.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 메시지 생성 과정에서 공통으로 사용하는 컨텍스트.
 */
public class MessageContext {

    private final Map<String, Object> values;

    public MessageContext(Map<String, Object> values) {
        this.values = values == null ? new HashMap<>() : new HashMap<>(values);
    }

    public static MessageContext empty() {
        return new MessageContext(Collections.emptyMap());
    }

    public static MessageContext of(Map<String, Object> values) {
        return new MessageContext(values);
    }

    public String getString(String key) {
        Object value = values.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    public Map<String, Object> data() {
        return Collections.unmodifiableMap(values);
    }
}
