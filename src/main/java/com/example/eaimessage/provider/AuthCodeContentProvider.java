package com.example.eaimessage.provider;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.PreparedMessageContent;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.AuthService;
import org.springframework.stereotype.Component;

@Component
public class AuthCodeContentProvider implements MessageContentProvider {

    private final AuthService authService;
    private final DefaultReceiverInfoResolver receiverInfoResolver;

    public AuthCodeContentProvider(AuthService authService, DefaultReceiverInfoResolver receiverInfoResolver) {
        this.authService = authService;
        this.receiverInfoResolver = receiverInfoResolver;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.AUTH_CODE;
    }

    @Override
    public PreparedMessageContent provide(TalkRequest request) {
        String receiverId = request.getReceiverId();
        String authCode = authService.getAuthCode();
        return new PreparedMessageContent(
            "AUTH_CODE",
            receiverInfoResolver.resolveType(receiverId),
            receiverInfoResolver.resolveAddress(receiverId),
            receiverId,
            "[인증번호]",
            "인증번호는 [" + authCode + "] 입니다."
        );
    }
}
