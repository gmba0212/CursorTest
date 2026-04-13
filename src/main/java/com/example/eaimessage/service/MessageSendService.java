package com.example.eaimessage.service;

import com.example.eaimessage.client.EaiHttpClient;
import com.example.eaimessage.factory.MessageGeneratorFactory;
import com.example.eaimessage.model.HttpSendRequest;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSendService {

    private final MessageGeneratorFactory messageGeneratorFactory;
    private final EaiHttpClient eaiHttpClient;
    private final String eaiEndpoint;

    public MessageSendService(
        MessageGeneratorFactory messageGeneratorFactory,
        EaiHttpClient eaiHttpClient,
        @Value("${eai.endpoint:http://localhost:8081/eai/send}") String eaiEndpoint
    ) {
        this.messageGeneratorFactory = messageGeneratorFactory;
        this.eaiHttpClient = eaiHttpClient;
        this.eaiEndpoint = eaiEndpoint;
    }

    public void send(TalkRequest request) {
        validateRequest(request);
        String payload = messageGeneratorFactory.generate(request);
        eaiHttpClient.send(new HttpSendRequest(eaiEndpoint, payload));
    }

    private static void validateRequest(TalkRequest request) {
        if (request == null || request.getMessageType() == null) {
            throw new IllegalArgumentException("messageType must not be null");
        }
        if (request.getReceiverId() == null || request.getReceiverId().isBlank()) {
            throw new IllegalArgumentException("receiverId must not be blank");
        }
    }
}
