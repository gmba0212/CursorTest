package com.example.eaimessage.renderer;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.SmsBodyPayload;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class SmsRenderer extends AbstractChannelRendererSupport implements ChannelMessageRenderer {

    @Override
    public ChannelType channelType() {
        return ChannelType.SMS;
    }

    @Override
    public String renderBody(TalkRequest request, ServiceData serviceData, MessageContent content) {
        String title = firstNonBlank(content.getSubject(), request.getTitle(), "알림");
        String text = firstNonBlank(content.getBodyText(), request.getContent(), "SMS 안내 메시지입니다.");
        SmsBodyPayload payload = new SmsBodyPayload(
            param(request, "smsChannel", "SMS"),
            defaultString(request.getReceiverType()),
            defaultString(request.getReceiverAddress()),
            defaultString(request.getReceiverId()),
            title,
            text
        );
        return payload.buildMessage();
    }
}
