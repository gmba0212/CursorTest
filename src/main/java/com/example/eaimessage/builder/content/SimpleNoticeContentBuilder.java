package com.example.eaimessage.builder.content;

import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class SimpleNoticeContentBuilder extends AbstractMessageContentSupport implements MessageContentBuilder {

    private static final String DEFAULT_TEMPLATE_CODE = "KTALK_NOTICE";

    @Override
    public MessageType supportedType() {
        return MessageType.SIMPLE_NOTICE;
    }

    @Override
    public MessageContent build(TalkRequest request, ServiceData serviceData) {
        return MessageContent.of(
            DEFAULT_TEMPLATE_CODE,
            defaultString(request.getTitle()),
            defaultString(request.getContent())
        );
    }
}
