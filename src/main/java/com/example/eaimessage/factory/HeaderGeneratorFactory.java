package com.example.eaimessage.factory;

import com.example.eaimessage.generator.header.EaiHeaderGenerator;
import com.example.eaimessage.model.ChannelType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HeaderGeneratorFactory {

    private static final Logger log = LoggerFactory.getLogger(HeaderGeneratorFactory.class);

    private final Map<ChannelType, EaiHeaderGenerator> generatorMap = new EnumMap<>(ChannelType.class);

    public HeaderGeneratorFactory(List<EaiHeaderGenerator> generators) {
        for (EaiHeaderGenerator generator : generators) {
            ChannelType channelType = generator.supportChannelType();
            if (generatorMap.put(channelType, generator) != null) {
                log.error("EaiHeaderGenerator 중복 등록 channel={}", channelType);
                throw new IllegalStateException("Duplicate EaiHeaderGenerator for " + channelType);
            }
        }
        log.info("HeaderGeneratorFactory 초기화 완료 등록 건수={}", generators.size());
    }

    public EaiHeaderGenerator get(ChannelType channelType) {
        EaiHeaderGenerator generator = generatorMap.get(channelType);
        if (generator == null) {
            log.warn("채널에 대한 EaiHeaderGenerator 없음 channel={}", channelType);
            throw new IllegalArgumentException("No EaiHeaderGenerator for " + channelType);
        }
        log.trace("EaiHeaderGenerator 선택 channel={}, class={}", channelType, generator.getClass().getSimpleName());
        return generator;
    }
}
