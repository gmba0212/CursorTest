package com.example.eaimessage.builder.factory;

import com.example.eaimessage.builder.body.BodyBuilder;
import com.example.eaimessage.model.MessageType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class BodyBuilderFactory {

    private final Map<MessageType, BodyBuilder> byType = new EnumMap<>(MessageType.class);

    public BodyBuilderFactory(List<BodyBuilder> builders) {
        for (BodyBuilder builder : builders) {
            MessageType type = builder.supportedType();
            if (byType.put(type, builder) != null) {
                throw new IllegalStateException("Duplicate BodyBuilder for " + type);
            }
        }
    }

    public BodyBuilder get(MessageType type) {
        if (type == null) {
            throw new IllegalArgumentException("messageType must not be null");
        }
        BodyBuilder builder = byType.get(type);
        if (builder == null) {
            throw new IllegalArgumentException("No BodyBuilder for " + type);
        }
        return builder;
    }
}
