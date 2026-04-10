package com.example.eaimessage.service;

import com.example.eaimessage.client.EaiHttpClient;
import com.example.eaimessage.generator.body.factory.MessageBodyGeneratorFactory;
import com.example.eaimessage.generator.header.factory.MessageHeaderGeneratorFactory;
import com.example.eaimessage.generator.header.MessageHeaderGenerator;
import com.example.eaimessage.config.KTalkProperties;
import com.example.eaimessage.model.BodyGenerationInput;
import com.example.eaimessage.model.HttpSendRequest;
import com.example.eaimessage.model.PreparedMessageContent;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.provider.factory.MessageContentProviderFactory;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSendService {

    private static final DateTimeFormatter TX_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private final MessageContentProviderFactory contentProviderFactory;
    private final MessageBodyGeneratorFactory bodyGeneratorFactory;
    private final MessageHeaderGeneratorFactory headerGeneratorFactory;
    private final String defaultSenderKey;
    private final EaiHttpClient eaiHttpClient;
    private final String eaiEndpoint;

    public MessageSendService(
        MessageContentProviderFactory contentProviderFactory,
        MessageBodyGeneratorFactory bodyGeneratorFactory,
        MessageHeaderGeneratorFactory headerGeneratorFactory,
        KTalkProperties kTalkProperties,
        EaiHttpClient eaiHttpClient,
        @Value("${eai.endpoint:http://localhost:8081/eai/send}") String eaiEndpoint
    ) {
        this.contentProviderFactory = contentProviderFactory;
        this.bodyGeneratorFactory = bodyGeneratorFactory;
        this.headerGeneratorFactory = headerGeneratorFactory;
        this.defaultSenderKey = kTalkProperties.getSenderKey();
        this.eaiHttpClient = eaiHttpClient;
        this.eaiEndpoint = eaiEndpoint;
    }

    public void send(TalkRequest request) {
        validateRequest(request);

        PreparedMessageContent preparedContent = contentProviderFactory
            .get(request.getChannelType(), request.getMessageType())
            .provide(request);

        String body = bodyGeneratorFactory
            .get(request.getChannelType(), request.getMessageType())
            .generate(buildBodyInput(request, preparedContent));
        MessageHeaderGenerator headerGenerator = headerGeneratorFactory.get();
        String header = headerGenerator.generate(
            newTransactionId(),
            request.getChannelType(),
            request.getMessageType(),
            utf8Length(body)
        );

        eaiHttpClient.send(new HttpSendRequest(eaiEndpoint, header + body));
    }

    private BodyGenerationInput buildBodyInput(TalkRequest request, PreparedMessageContent content) {
        return new BodyGenerationInput(
            request.getChannelType(),
            request.getMessageType(),
            content.receiverType(),
            content.receiverAddress(),
            content.receiverId(),
            content.templateCode(),
            defaultSenderKey,
            content.subject(),
            content.content()
        );
    }

    private static void validateRequest(TalkRequest request) {
        if (request == null || request.getChannelType() == null || request.getMessageType() == null) {
            throw new IllegalArgumentException("channelType/messageType must not be null");
        }
        if (request.getReceiverId() == null || request.getReceiverId().isBlank()) {
            throw new IllegalArgumentException("receiverId must not be blank");
        }
    }

    private static String newTransactionId() {
        return LocalDateTime.now().format(TX_ID_FORMAT);
    }

    private static int utf8Length(String body) {
        return (body == null ? "" : body).getBytes(StandardCharsets.UTF_8).length;
    }
}
