package com.example.eaimessage.factory;

import com.example.eaimessage.content.MessageContentDto;
import com.example.eaimessage.content.MessageContentProvider;
import com.example.eaimessage.generator.body.EaiBodyGenerator;
import com.example.eaimessage.generator.header.EaiHeaderGenerator;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageGeneratorFactory {

    private static final DateTimeFormatter TX_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final Map<MessageType, MessageContentProvider> providerMap = new EnumMap<>(MessageType.class);
    private final EaiHeaderGenerator headerGenerator;
    private final EaiBodyGenerator bodyGenerator;

    public MessageGeneratorFactory(
        List<MessageContentProvider> providers,
        EaiHeaderGenerator headerGenerator,
        EaiBodyGenerator bodyGenerator
    ) {
        this.headerGenerator = headerGenerator;
        this.bodyGenerator = bodyGenerator;

        for (MessageContentProvider provider : providers) {
            MessageType messageType = provider.supportType();
            if (providerMap.put(messageType, provider) != null) {
                throw new IllegalStateException("Duplicate MessageContentProvider for " + messageType);
            }
        }
    }

    public String generate(TalkRequest request) {
        MessageContentProvider provider = providerMap.get(request.getMessageType());
        if (provider == null) {
            throw new IllegalArgumentException("No MessageContentProvider for " + request.getMessageType());
        }

        MessageContentDto contentDto = provider.getContent(request);
        String body = bodyGenerator.generate(request, contentDto);
        String header = headerGenerator.generate(
            LocalDateTime.now().format(TX_ID_FORMAT),
            request.getMessageType(),
            utf8Length(body),
            contentDto.title(),
            contentDto.content()
        );
        return header + body;
    }

    private static int utf8Length(String body) {
        return (body == null ? "" : body).getBytes(StandardCharsets.UTF_8).length;
    }
}
