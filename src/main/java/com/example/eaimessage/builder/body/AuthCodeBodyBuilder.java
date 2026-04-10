package com.example.eaimessage.builder.body;

import com.example.eaimessage.config.KTalkProperties;
import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthCodeBodyBuilder extends AbstractBodyBuilderSupport implements BodyBuilder {

    private final KTalkProperties kTalkProperties;

    public AuthCodeBodyBuilder(KTalkProperties kTalkProperties) {
        this.kTalkProperties = kTalkProperties;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.AUTH_CODE;
    }

    @Override
    public String build(TalkRequest request, MessageContext context) {
        return buildByChannel(
            request,
            kTalkProperties,
            "AUTH_CODE",
            "[인증번호]",
            "인증번호는 [" + valueOr(context, "authCode", "000000") + "] 입니다."
        );
    }
}
