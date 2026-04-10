package com.example.eaimessage.service.bodyfield;

import com.example.eaimessage.model.MessageType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageBodyFieldResolverRegistry {

    private final Map<MessageType, MessageBodyFieldResolver> byType;

    public MessageBodyFieldResolverRegistry(List<MessageBodyFieldResolver> resolvers) {
        EnumMap<MessageType, MessageBodyFieldResolver> map = new EnumMap<>(MessageType.class);
        for (MessageBodyFieldResolver resolver : resolvers) {
            MessageType type = resolver.supportedType();
            if (map.put(type, resolver) != null) {
                throw new IllegalStateException("Duplicate MessageBodyFieldResolver for " + type);
            }
        }
        this.byType = Map.copyOf(map);
    }

    public MessageBodyFieldResolver require(MessageType type) {
        if (type == null) {
            throw new IllegalArgumentException("messageType must not be null");
        }
        MessageBodyFieldResolver resolver = byType.get(type);
        if (resolver == null) {
            throw new IllegalArgumentException("No MessageBodyFieldResolver for " + type);
        }
        return resolver;
    }
}
