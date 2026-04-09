package com.example.eaimessage.resolver;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.AuthService;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AuthCodeDataResolver extends AbstractResolverSupport implements MessageDataResolver {

    private final AuthService authService;

    public AuthCodeDataResolver(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.AUTH_CODE;
    }

    @Override
    public ServiceData resolve(TalkRequest request) {
        return new ServiceData(Map.of("authCode", authService.getAuthCode()));
    }
}
