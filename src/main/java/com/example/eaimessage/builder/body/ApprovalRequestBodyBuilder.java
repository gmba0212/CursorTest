package com.example.eaimessage.builder.body;

import com.example.eaimessage.config.KTalkProperties;
import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class ApprovalRequestBodyBuilder extends AbstractBodyBuilderSupport implements BodyBuilder {

    private final KTalkProperties kTalkProperties;

    public ApprovalRequestBodyBuilder(KTalkProperties kTalkProperties) {
        this.kTalkProperties = kTalkProperties;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.APPROVAL_REQUEST;
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
