package com.example.eaimessage.builder.factory;

import com.example.eaimessage.builder.header.HeaderBuilder;
import org.springframework.stereotype.Component;

@Component
public class HeaderBuilderFactory {

    private final HeaderBuilder defaultHeaderBuilder;

    public HeaderBuilderFactory(HeaderBuilder defaultHeaderBuilder) {
        this.defaultHeaderBuilder = defaultHeaderBuilder;
    }

    public HeaderBuilder get() {
        return defaultHeaderBuilder;
    }
}
