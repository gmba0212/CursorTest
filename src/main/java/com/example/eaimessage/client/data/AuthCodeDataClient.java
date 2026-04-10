package com.example.eaimessage.client.data;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.AuthService;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AuthCodeDataClient implements MessageDataClient {

    private final AuthService authService;

    public AuthCodeDataClient(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.AUTH_CODE;
    }

    @Override
    public MessageContext fetch(TalkRequest request) {
        return MessageContext.of(Map.of("authCode", authService.getAuthCode()));
    }
}
