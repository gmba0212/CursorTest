package com.example.eaimessage.service.bodyfield;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthCodeBodyFieldResolver implements MessageBodyFieldResolver {

    @Override
    public MessageType supportedType() {
        return MessageType.AUTH_CODE;
    }

    @Override
    public BodyDisplayFields resolve(TalkRequest request, MessageContext rawContext) {
        String code = BodyFieldResolveSupport.valueOr(rawContext, "authCode", "000000");
        return BodyDisplayFields.of(
            "AUTH_CODE",
            "[인증번호]",
            "인증번호는 [" + code + "] 입니다."
        );
    }
}
