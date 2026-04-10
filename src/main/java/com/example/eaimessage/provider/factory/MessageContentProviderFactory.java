package com.example.eaimessage.provider.factory;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageRouteKey;
import com.example.eaimessage.provider.MessageContentProvider;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageContentProviderFactory {

    private final Map<MessageRouteKey, MessageContentProvider> byRoute = new HashMap<>();

    public MessageContentProviderFactory(List<MessageContentProvider> providers) {
        for (MessageContentProvider provider : providers) {
            MessageType type = provider.supportedType();
            for (ChannelType channelType : EnumSet.allOf(ChannelType.class)) {
                MessageRouteKey routeKey = new MessageRouteKey(channelType, type);
                if (byRoute.put(routeKey, provider) != null) {
                    throw new IllegalStateException("Duplicate MessageContentProvider for " + routeKey);
                }
            }
        }
    }

    public MessageContentProvider get(ChannelType channelType, MessageType messageType) {
        if (channelType == null) {
            throw new IllegalArgumentException("channelType must not be null");
        }
        if (messageType == null) {
            throw new IllegalArgumentException("messageType must not be null");
        }
        MessageContentProvider provider = byRoute.get(new MessageRouteKey(channelType, messageType));
        if (provider == null) {
            throw new IllegalArgumentException("No MessageContentProvider for " + channelType + "/" + messageType);
        }
        return provider;
    }
}
