package com.example.eaimessage.service;

import com.example.eaimessage.client.EaiHttpClient;
import com.example.eaimessage.header.EaiHeaderFactory;
import com.example.eaimessage.model.HttpSendRequest;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.registry.ChannelRendererRegistry;
import com.example.eaimessage.registry.MessageContentBuilderRegistry;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSendService {

    private final ExternalMessageDataService externalMessageDataService;
    private final MessageContentBuilderRegistry contentRegistry;
    private final ChannelRendererRegistry channelRegistry;
    private final EaiHeaderFactory headerFactory;
    private final EaiHttpClient eaiHttpClient;
    private final String eaiEndpoint;

    public MessageSendService(
        ExternalMessageDataService externalMessageDataService,
        MessageContentBuilderRegistry contentRegistry,
        ChannelRendererRegistry channelRegistry,
        EaiHeaderFactory headerFactory,
        EaiHttpClient eaiHttpClient,
        @Value("${eai.endpoint:http://localhost:8081/eai/send}") String eaiEndpoint
    ) {
        this.externalMessageDataService = externalMessageDataService;
        this.contentRegistry = contentRegistry;
        this.channelRegistry = channelRegistry;
        this.headerFactory = headerFactory;
        this.eaiHttpClient = eaiHttpClient;
        this.eaiEndpoint = eaiEndpoint;
    }

    public void send(TalkRequest request) {
        if (request == null || request.getChannelType() == null || request.getMessageType() == null) {
            throw new IllegalArgumentException("channelType/messageType must not be null");
        }
        var serviceData = externalMessageDataService.resolve(request);
        var content = contentRegistry.require(request.getMessageType()).build(request, serviceData);
        String body = channelRegistry.require(request.getChannelType())
            .renderBody(request, serviceData, content);

        String header = headerFactory.createHeader(
            newTransactionId(),
            request.getChannelType(),
            request.getMessageType(),
            utf8Length(body)
        );
        eaiHttpClient.send(new HttpSendRequest(eaiEndpoint, header + body));
    }

    private static String newTransactionId() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private static int utf8Length(String body) {
        return (body == null ? "" : body).getBytes(StandardCharsets.UTF_8).length;
    }
}
