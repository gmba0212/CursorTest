package com.example.eaimessage.factory;

import com.example.eaimessage.generator.body.BodyGenerationResult;
import com.example.eaimessage.generator.body.EaiBodyGenerator;
import com.example.eaimessage.generator.header.EaiHeaderGenerator;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageGeneratorFactory {

    private final Map<ChannelType, EaiHeaderGenerator> headerGeneratorMap = new EnumMap<>(ChannelType.class);
    private final Map<MessageType, EaiBodyGenerator> bodyGeneratorMap = new EnumMap<>(MessageType.class);

    public MessageGeneratorFactory(
        List<EaiHeaderGenerator> headerGenerators,
        List<EaiBodyGenerator> bodyGenerators
    ) {
        for (EaiHeaderGenerator headerGenerator : headerGenerators) {
            ChannelType channelType = headerGenerator.supportChannelType();
            if (headerGeneratorMap.put(channelType, headerGenerator) != null) {
                throw new IllegalStateException("Duplicate EaiHeaderGenerator for " + channelType);
            }
        }

        for (EaiBodyGenerator bodyGenerator : bodyGenerators) {
            MessageType messageType = bodyGenerator.supportMessageType();
            if (bodyGeneratorMap.put(messageType, bodyGenerator) != null) {
                throw new IllegalStateException("Duplicate EaiBodyGenerator for " + messageType);
            }
        }
    }

    public String generate(TalkRequest request) {
        EaiHeaderGenerator headerGenerator = getHeaderGenerator(request.getChannelType());
        EaiBodyGenerator bodyGenerator = getBodyGenerator(request.getMessageType());

        BodyGenerationResult bodyResult = bodyGenerator.generate(request);
        String header = headerGenerator.generate(
            request,
            bodyResult.title(),
            bodyResult.content(),
            utf8Length(bodyResult.body())
        );

        return header + bodyResult.body();
    }

    private EaiHeaderGenerator getHeaderGenerator(ChannelType channelType) {
        EaiHeaderGenerator generator = headerGeneratorMap.get(channelType);
        if (generator == null) {
            throw new IllegalArgumentException("No EaiHeaderGenerator for " + channelType);
        }
        return generator;
    }

    private EaiBodyGenerator getBodyGenerator(MessageType messageType) {
        EaiBodyGenerator generator = bodyGeneratorMap.get(messageType);
        if (generator == null) {
            throw new IllegalArgumentException("No EaiBodyGenerator for " + messageType);
        }
        return generator;
    }

    private static int utf8Length(String body) {
        return (body == null ? "" : body).getBytes(StandardCharsets.UTF_8).length;
    }
}
