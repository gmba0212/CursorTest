package com.example.eaimessage.factory;

import com.example.eaimessage.content.MessageContentDto;
import com.example.eaimessage.content.MessageContentProvider;
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

    private final Map<MessageType, MessageContentProvider> providerMap = new EnumMap<>(MessageType.class);
    private final Map<ChannelType, EaiHeaderGenerator> headerGeneratorMap = new EnumMap<>(ChannelType.class);
    private final Map<ChannelType, EaiBodyGenerator> bodyGeneratorMap = new EnumMap<>(ChannelType.class);

    public MessageGeneratorFactory(
        List<MessageContentProvider> providers,
        List<EaiHeaderGenerator> headerGenerators,
        List<EaiBodyGenerator> bodyGenerators
    ) {
        for (MessageContentProvider provider : providers) {
            MessageType messageType = provider.supportType();
            if (providerMap.put(messageType, provider) != null) {
                throw new IllegalStateException("Duplicate MessageContentProvider for " + messageType);
            }
        }

        for (EaiHeaderGenerator headerGenerator : headerGenerators) {
            ChannelType channelType = headerGenerator.supportChannelType();
            if (headerGeneratorMap.put(channelType, headerGenerator) != null) {
                throw new IllegalStateException("Duplicate EaiHeaderGenerator for " + channelType);
            }
        }

        for (EaiBodyGenerator bodyGenerator : bodyGenerators) {
            ChannelType channelType = bodyGenerator.supportChannelType();
            if (bodyGeneratorMap.put(channelType, bodyGenerator) != null) {
                throw new IllegalStateException("Duplicate EaiBodyGenerator for " + channelType);
            }
        }
    }

    public String generate(TalkRequest request) {
        MessageContentProvider provider = getContentProvider(request.getMessageType());
        MessageContentDto contentDto = provider.getContent(request);

        EaiHeaderGenerator headerGenerator = getHeaderGenerator(request.getChannelType());
        EaiBodyGenerator bodyGenerator = getBodyGenerator(request.getChannelType());

        String body = bodyGenerator.generate(request, contentDto);
        String header = headerGenerator.generate(request, contentDto.title(), contentDto.content(), utf8Length(body));
        return header + body;
    }

    private MessageContentProvider getContentProvider(MessageType messageType) {
        MessageContentProvider provider = providerMap.get(messageType);
        if (provider == null) {
            throw new IllegalArgumentException("No MessageContentProvider for " + messageType);
        }
        return provider;
    }

    private EaiHeaderGenerator getHeaderGenerator(ChannelType channelType) {
        EaiHeaderGenerator generator = headerGeneratorMap.get(channelType);
        if (generator == null) {
            throw new IllegalArgumentException("No EaiHeaderGenerator for " + channelType);
        }
        return generator;
    }

    private EaiBodyGenerator getBodyGenerator(ChannelType channelType) {
        EaiBodyGenerator generator = bodyGeneratorMap.get(channelType);
        if (generator == null) {
            throw new IllegalArgumentException("No EaiBodyGenerator for " + channelType);
        }
        return generator;
    }

    private static int utf8Length(String body) {
        return (body == null ? "" : body).getBytes(StandardCharsets.UTF_8).length;
    }
}
