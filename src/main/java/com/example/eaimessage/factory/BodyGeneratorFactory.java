package com.example.eaimessage.factory;

import com.example.eaimessage.generator.body.EaiBodyGenerator;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BodyGeneratorFactory {

    private static final Logger log = LoggerFactory.getLogger(BodyGeneratorFactory.class);

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
                log.error("EaiBodyGenerator 중복 등록 channel={}, messageType={}", channelType, messageType);
                throw new IllegalStateException("Duplicate EaiBodyGenerator for " + channelType + ":" + messageType);
            }
        }
        log.info("BodyGeneratorFactory 초기화 완료 등록 건수={}", generators.size());
    }

    public EaiBodyGenerator get(ChannelType channelType, MessageType messageType) {
        Map<MessageType, EaiBodyGenerator> messageGeneratorMap = generatorMap.get(channelType);
        if (messageGeneratorMap == null) {
            log.warn("채널에 대한 EaiBodyGenerator 없음 channel={}", channelType);
            throw new IllegalArgumentException("No EaiBodyGenerator for channel " + channelType);
        }
        EaiBodyGenerator generator = messageGeneratorMap.get(messageType);
        if (generator == null) {
            log.warn("메시지 타입에 대한 EaiBodyGenerator 없음 channel={}, messageType={}", channelType, messageType);
            throw new IllegalArgumentException("No EaiBodyGenerator for " + channelType + ":" + messageType);
        }
        log.trace("EaiBodyGenerator 선택 channel={}, messageType={}, class={}", channelType, messageType, generator.getClass().getSimpleName());
        return generator;
    }
}
