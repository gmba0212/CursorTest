package com.example.eaimessage.registry;

import com.example.eaimessage.builder.content.MessageContentBuilder;
import com.example.eaimessage.model.MessageType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageContentBuilderRegistry {

    private final Map<MessageType, MessageContentBuilder> byType = new EnumMap<>(MessageType.class);

    public MessageContentBuilderRegistry(List<MessageContentBuilder> builders) {
        for (MessageContentBuilder b : builders) {
            MessageType t = b.supportedType();
            if (byType.put(t, b) != null) {
                throw new IllegalStateException("Duplicate MessageContentBuilder for " + t);
            }
        }
    }

    public MessageContentBuilder require(MessageType type) {
        if (type == null) {
            throw new IllegalArgumentException("messageType must not be null");
        }
        MessageContentBuilder b = byType.get(type);
        if (b == null) {
            throw new IllegalArgumentException("No MessageContentBuilder for " + type);
        }
        return b;
    }
}
