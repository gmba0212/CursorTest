package com.example.eaimessage.registry;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.resolver.MessageDataResolver;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageDataResolverRegistry {

    private final Map<MessageType, MessageDataResolver> byType = new EnumMap<>(MessageType.class);

    public MessageDataResolverRegistry(List<MessageDataResolver> resolvers) {
        for (MessageDataResolver r : resolvers) {
            MessageType t = r.supportedType();
            if (byType.put(t, r) != null) {
                throw new IllegalStateException("Duplicate MessageDataResolver for " + t);
            }
        }
    }

    public MessageDataResolver require(MessageType type) {
        if (type == null) {
            throw new IllegalArgumentException("messageType must not be null");
        }
        MessageDataResolver r = byType.get(type);
        if (r == null) {
            throw new IllegalArgumentException("No MessageDataResolver for " + type);
        }
        return r;
    }
}
