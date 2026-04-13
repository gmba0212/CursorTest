package com.example.eaimessage.factory;

import com.example.eaimessage.generator.header.EaiHeaderGenerator;
import com.example.eaimessage.model.ChannelType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HeaderGeneratorFactory {

    private final Map<ChannelType, EaiHeaderGenerator> generatorMap = new EnumMap<>(ChannelType.class);

    public HeaderGeneratorFactory(List<EaiHeaderGenerator> generators) {
        for (EaiHeaderGenerator generator : generators) {
            ChannelType channelType = generator.supportChannelType();
            if (generatorMap.put(channelType, generator) != null) {
                throw new IllegalStateException("Duplicate EaiHeaderGenerator for " + channelType);
            }
        }
    }

    public EaiHeaderGenerator get(ChannelType channelType) {
        EaiHeaderGenerator generator = generatorMap.get(channelType);
        if (generator == null) {
            throw new IllegalArgumentException("No EaiHeaderGenerator for " + channelType);
        }
        return generator;
    }
}
