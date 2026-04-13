package com.example.eaimessage.service;

import com.example.eaimessage.client.EaiHttpClient;
import com.example.eaimessage.factory.BodyGeneratorFactory;
import com.example.eaimessage.factory.HeaderGeneratorFactory;
import com.example.eaimessage.generator.body.BodyData;
import com.example.eaimessage.generator.body.DefaultBodyTemplate;
import com.example.eaimessage.generator.header.DefaultHeaderTemplate;
import com.example.eaimessage.generator.header.HeaderData;
import com.example.eaimessage.model.HttpSendRequest;
import com.example.eaimessage.model.TalkRequest;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSendService {

    private final HeaderGeneratorFactory headerGeneratorFactory;
    private final BodyGeneratorFactory bodyGeneratorFactory;
    private final DefaultHeaderTemplate defaultHeaderTemplate;
    private final DefaultBodyTemplate defaultBodyTemplate;
    private final EaiHttpClient eaiHttpClient;
    private final String eaiEndpoint;

    public MessageSendService(
        HeaderGeneratorFactory headerGeneratorFactory,
        BodyGeneratorFactory bodyGeneratorFactory,
        DefaultHeaderTemplate defaultHeaderTemplate,
        DefaultBodyTemplate defaultBodyTemplate,
        EaiHttpClient eaiHttpClient,
        @Value("${eai.endpoint:http://localhost:8081/eai/send}") String eaiEndpoint
    ) {
        this.headerGeneratorFactory = headerGeneratorFactory;
        this.bodyGeneratorFactory = bodyGeneratorFactory;
        this.defaultHeaderTemplate = defaultHeaderTemplate;
        this.defaultBodyTemplate = defaultBodyTemplate;
        this.eaiHttpClient = eaiHttpClient;
        this.eaiEndpoint = eaiEndpoint;
    }

    public void send(TalkRequest request) {
        validateRequest(request);

        BodyData bodyData = bodyGeneratorFactory.get(request.getMessageType()).generate(request);
        String body = defaultBodyTemplate.generate(bodyData);

        HeaderData headerData = headerGeneratorFactory
            .get(request.getChannelType())
            .generate(request, bodyData, utf8Length(body));
        String header = defaultHeaderTemplate.generate(headerData);

        String finalMessage = header + body;
        eaiHttpClient.send(new HttpSendRequest(eaiEndpoint, finalMessage));
    }

    private static int utf8Length(String body) {
        return (body == null ? "" : body).getBytes(StandardCharsets.UTF_8).length;
    }

    private static void validateRequest(TalkRequest request) {
        if (request == null || request.getChannelType() == null) {
            throw new IllegalArgumentException("channelType must not be null");
        }
        if (request.getMessageType() == null) {
            throw new IllegalArgumentException("messageType must not be null");
        }
        if (request.getReceiverId() == null || request.getReceiverId().isBlank()) {
            throw new IllegalArgumentException("receiverId must not be blank");
        }
    }
}
