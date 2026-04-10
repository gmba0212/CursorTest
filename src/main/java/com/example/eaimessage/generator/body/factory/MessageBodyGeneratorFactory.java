package com.example.eaimessage.generator.body.factory;

import com.example.eaimessage.generator.body.MessageBodyGenerator;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageRouteKey;
import com.example.eaimessage.model.MessageType;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageBodyGeneratorFactory {

    private final Map<MessageRouteKey, MessageBodyGenerator> byRoute = new HashMap<>();

    public MessageBodyGeneratorFactory(MessageBodyGenerator defaultMessageBodyGenerator) {
        for (ChannelType channelType : EnumSet.allOf(ChannelType.class)) {
            for (MessageType messageType : EnumSet.allOf(MessageType.class)) {
                byRoute.put(new MessageRouteKey(channelType, messageType), defaultMessageBodyGenerator);
            }
        }
    }

    public MessageBodyGenerator get(ChannelType channelType, MessageType messageType) {
        MessageBodyGenerator generator = byRoute.get(new MessageRouteKey(channelType, messageType));
        if (generator == null) {
            throw new IllegalArgumentException("No MessageBodyGenerator for " + channelType + "/" + messageType);
        }
        return generator;
    }
}
