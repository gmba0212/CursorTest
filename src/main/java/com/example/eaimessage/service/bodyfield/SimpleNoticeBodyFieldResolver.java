package com.example.eaimessage.service.bodyfield;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class SimpleNoticeBodyFieldResolver implements MessageBodyFieldResolver {

    @Override
    public MessageType supportedType() {
        return MessageType.SIMPLE_NOTICE;
    }

    @Override
    public BodyDisplayFields resolve(TalkRequest request, MessageContext rawContext) {
        return BodyDisplayFields.of(
            "KTALK_NOTICE",
            BodyFieldResolveSupport.defaultString(request.getTitle()),
            BodyFieldResolveSupport.defaultString(request.getContent())
        );
    }
}
