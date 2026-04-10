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
            context.getString("templateCode"),
            context.getString("receiverType"),
            context.getString("subject"),
            context.getString("content")
        );
    }
}
