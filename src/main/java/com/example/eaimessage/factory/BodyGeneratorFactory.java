package com.example.eaimessage.factory;

import com.example.eaimessage.generator.body.EaiBodyGenerator;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class BodyGeneratorFactory {

    private final Map<ChannelType, Map<MessageType, EaiBodyGenerator>> generatorMap = new EnumMap<>(ChannelType.class);

    public BodyGeneratorFactory(List<EaiBodyGenerator> generators) {
        for (EaiBodyGenerator generator : generators) {
            ChannelType channelType = generator.supportChannelType();
            MessageType messageType = generator.supportMessageType();
            Map<MessageType, EaiBodyGenerator> messageGeneratorMap = generatorMap.computeIfAbsent(
                channelType,
                ignored -> new EnumMap<>(MessageType.class)
            );
            if (messageGeneratorMap.put(messageType, generator) != null) {
                throw new IllegalStateException("Duplicate EaiBodyGenerator for " + channelType + ":" + messageType);
            }
        }
    }

    public EaiBodyGenerator get(ChannelType channelType, MessageType messageType) {
        Map<MessageType, EaiBodyGenerator> messageGeneratorMap = generatorMap.get(channelType);
        if (messageGeneratorMap == null) {
            throw new IllegalArgumentException("No EaiBodyGenerator for channel " + channelType);
        }
        EaiBodyGenerator generator = messageGeneratorMap.get(messageType);
        if (generator == null) {
            throw new IllegalArgumentException("No EaiBodyGenerator for " + channelType + ":" + messageType);
        }
        return generator;
    }
}
