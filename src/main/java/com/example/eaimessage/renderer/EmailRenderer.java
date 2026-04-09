package com.example.eaimessage.renderer;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.EmailBodyPayload;
import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class EmailRenderer extends AbstractChannelRendererSupport implements ChannelMessageRenderer {

    @Override
    public ChannelType channelType() {
        return ChannelType.EMAIL;
    }

    @Override
    public String renderBody(TalkRequest request, ServiceData serviceData, MessageContent content) {
        EmailBodyPayload payload = new EmailBodyPayload(
            firstNonBlank(content.getSubject(), request.getTitle(), "[메일]"),
            firstNonBlank(request.getReceiverType(), "USER"),
            defaultString(request.getReceiverAddress()),
            defaultString(request.getReceiverId()),
            firstNonBlank(content.getBodyText(), request.getContent(), "")
        );
        return payload.buildMessage();
    }
}
