package com.example.eaimessage.service;

import com.example.eaimessage.builder.factory.BodyBuilderFactory;
import com.example.eaimessage.builder.factory.HeaderBuilderFactory;
import com.example.eaimessage.client.EaiHttpClient;
import com.example.eaimessage.client.data.MessageDataClient;
import com.example.eaimessage.model.HttpSendRequest;
import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSendService {

    private final BodyBuilderFactory bodyBuilderFactory;
    private final HeaderBuilderFactory headerBuilderFactory;
    private final MessageContentComposer messageContentComposer;
    private final EaiHttpClient eaiHttpClient;
    private final String eaiEndpoint;
    private final Map<MessageType, MessageDataClient> dataClients = new EnumMap<>(MessageType.class);

    public MessageSendService(
        List<MessageDataClient> clients,
        BodyBuilderFactory bodyBuilderFactory,
        HeaderBuilderFactory headerBuilderFactory,
        MessageContentComposer messageContentComposer,
        EaiHttpClient eaiHttpClient,
        @Value("${eai.endpoint:http://localhost:8081/eai/send}") String eaiEndpoint
    ) {
        for (MessageDataClient client : clients) {
            MessageType type = client.supportedType();
            if (dataClients.put(type, client) != null) {
                throw new IllegalStateException("Duplicate MessageDataClient for " + type);
            }
        }
        this.bodyBuilderFactory = bodyBuilderFactory;
        this.headerBuilderFactory = headerBuilderFactory;
        this.messageContentComposer = messageContentComposer;
        this.eaiHttpClient = eaiHttpClient;
        this.eaiEndpoint = eaiEndpoint;
    }

    public void send(TalkRequest request) {
        if (request == null || request.getChannelType() == null || request.getMessageType() == null) {
            throw new IllegalArgumentException("channelType/messageType must not be null");
        }

        MessageContext context = messageContentComposer.compose(request, resolveContext(request));
        String body = bodyBuilderFactory.get(request.getMessageType()).build(request, context);
        String header = headerBuilderFactory.get().build(
            newTransactionId(),
            request.getChannelType(),
            request.getMessageType(),
            utf8Length(body)
        );

        eaiHttpClient.send(new HttpSendRequest(eaiEndpoint, header + body));
    }

    private MessageContext resolveContext(TalkRequest request) {
        MessageDataClient dataClient = dataClients.get(request.getMessageType());
        if (dataClient == null) {
            throw new IllegalArgumentException("No MessageDataClient for " + request.getMessageType());
        }
        return dataClient.fetch(request);
    }

    private static String newTransactionId() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private static int utf8Length(String body) {
        return (body == null ? "" : body).getBytes(StandardCharsets.UTF_8).length;
    }
}
