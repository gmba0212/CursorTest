package com.example.eaimessage.service;

import com.example.eaimessage.builder.MessageBuilder;
import com.example.eaimessage.client.EaiHttpClient;
import com.example.eaimessage.factory.MessageBuilderFactory;
import com.example.eaimessage.model.HttpSendRequest;
import com.example.eaimessage.model.MessagePayload;
import com.example.eaimessage.model.MessageSendRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSendService {

    private final MessageBuilderFactory messageBuilderFactory;
    private final EaiHttpClient eaiHttpClient;
    private final String eaiEndpoint;

    public MessageSendService(
        MessageBuilderFactory messageBuilderFactory,
        EaiHttpClient eaiHttpClient,
        @Value("${eai.endpoint:http://localhost:8081/eai/send}") String eaiEndpoint
    ) {
        this.messageBuilderFactory = messageBuilderFactory;
        this.eaiHttpClient = eaiHttpClient;
        this.eaiEndpoint = eaiEndpoint;
    }

    public void send(MessageSendRequest request) {
        if (request == null || request.getChannelType() == null) {
            throw new IllegalArgumentException("channelType must not be null");
        }
        MessageBuilder builder = messageBuilderFactory.getBuilder(request.getChannelType());
        MessagePayload payload = builder.build(request);

        HttpSendRequest httpSendRequest = new HttpSendRequest(eaiEndpoint, payload.getPayload());
        eaiHttpClient.send(httpSendRequest);
    }
}
