package com.example.eaimessage.builder.body;

import com.example.eaimessage.config.KTalkProperties;
import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class SimpleNoticeBodyBuilder extends AbstractBodyBuilderSupport implements BodyBuilder {

    private static final String DEFAULT_TEMPLATE_CODE = "KTALK_NOTICE";

    private final KTalkProperties kTalkProperties;

    public SimpleNoticeBodyBuilder(KTalkProperties kTalkProperties) {
        this.kTalkProperties = kTalkProperties;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.SIMPLE_NOTICE;
    }

    @Override
    public String build(TalkRequest request, MessageContext context) {
        return buildByChannel(
            request,
            kTalkProperties,
            DEFAULT_TEMPLATE_CODE,
            defaultString(request.getTitle()),
            defaultString(request.getContent())
        );
    }
}
