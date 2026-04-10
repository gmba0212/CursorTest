package com.example.eaimessage.generator.header.factory;

import com.example.eaimessage.generator.header.MessageHeaderGenerator;
import org.springframework.stereotype.Component;

@Component
public class MessageHeaderGeneratorFactory {

    private final MessageHeaderGenerator generator;

    public MessageHeaderGeneratorFactory(MessageHeaderGenerator generator) {
        this.generator = generator;
    }

    public MessageHeaderGenerator get() {
        return generator;
    }
}
