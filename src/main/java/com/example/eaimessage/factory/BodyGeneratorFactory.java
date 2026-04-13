package com.example.eaimessage.factory;

import com.example.eaimessage.generator.body.EaiBodyGenerator;
import com.example.eaimessage.model.MessageType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class BodyGeneratorFactory {

    private final Map<MessageType, EaiBodyGenerator> generatorMap = new EnumMap<>(MessageType.class);

    public BodyGeneratorFactory(List<EaiBodyGenerator> generators) {
        for (EaiBodyGenerator generator : generators) {
            MessageType messageType = generator.supportMessageType();
            if (generatorMap.put(messageType, generator) != null) {
                throw new IllegalStateException("Duplicate EaiBodyGenerator for " + messageType);
            }
        }
    }

    public EaiBodyGenerator get(MessageType messageType) {
        EaiBodyGenerator generator = generatorMap.get(messageType);
        if (generator == null) {
            throw new IllegalArgumentException("No EaiBodyGenerator for " + messageType);
        }
        return generator;
    }
}
