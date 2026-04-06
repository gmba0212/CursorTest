package com.example.eaimessage.builder;

import com.example.eaimessage.model.ATalkBodySendData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageSendRequest;
import com.example.eaimessage.model.MessageType;
import org.springframework.stereotype.Component;

@Component
public class SmsMessageBuilder extends AbstractMessageBuilder {

    @Override
    protected void applyMessageType(ATalkBodySendData body, MessageSendRequest request) {
        MessageType messageType = request.getMessageType();
        if (messageType != MessageType.SMS_NOTICE) {
            throw new IllegalArgumentException("SMS 채널 미지원 messageType: " + messageType);
        }

        body.setTemplateCode("SMS_NOTICE");
        body.setSubject(defaultString(request.getSubject()));
        body.setContent(defaultString(request.getContent()));
    }

    @Override
    protected ChannelType channelType() {
        return ChannelType.SMS;
    }
}
