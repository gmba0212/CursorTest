package com.example.eaimessage.registry;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.renderer.ChannelMessageRenderer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ChannelRendererRegistry {

    private final Map<ChannelType, ChannelMessageRenderer> byChannel = new EnumMap<>(ChannelType.class);

    public ChannelRendererRegistry(List<ChannelMessageRenderer> renderers) {
        for (ChannelMessageRenderer r : renderers) {
            ChannelType c = r.channelType();
            if (byChannel.put(c, r) != null) {
                throw new IllegalStateException("Duplicate ChannelMessageRenderer for " + c);
            }
        }
    }

    public ChannelMessageRenderer require(ChannelType channelType) {
        if (channelType == null) {
            throw new IllegalArgumentException("channelType must not be null");
        }
        ChannelMessageRenderer r = byChannel.get(channelType);
        if (r == null) {
            throw new IllegalArgumentException("No ChannelMessageRenderer for " + channelType);
        }
        return r;
    }
}
