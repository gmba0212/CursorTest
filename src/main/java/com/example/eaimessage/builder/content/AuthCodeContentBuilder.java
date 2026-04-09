package com.example.eaimessage.builder.content;

import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthCodeContentBuilder extends AbstractMessageContentSupport implements MessageContentBuilder {

    @Override
    public MessageType supportedType() {
        return MessageType.AUTH_CODE;
    }

    @Override
    public MessageContent build(TalkRequest request, ServiceData serviceData) {
        return MessageContent.of(
            "AUTH_CODE",
            "[인증번호]",
            "인증번호는 [" + valueOr(serviceData, "authCode", "000000") + "] 입니다."
        );
    }
}
