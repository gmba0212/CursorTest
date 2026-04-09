package com.example.eaimessage.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServiceData {
    private final Map<String, Object> values;

    public ServiceData(Map<String, Object> values) {
        this.values = values == null ? new HashMap<>() : new HashMap<>(values);
    }

    public static ServiceData empty() {
        return new ServiceData(Collections.emptyMap());
    }

    public static ServiceData of(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return new ServiceData(map);
    }

    public static ServiceData of(Map<String, Object> values) {
        return new ServiceData(values);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public String getString(String key) {
        Object value = values.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    public Map<String, Object> data() {
        return Collections.unmodifiableMap(values);
    }
}
