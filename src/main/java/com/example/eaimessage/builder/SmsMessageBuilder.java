package com.example.eaimessage.builder;

import com.example.eaimessage.model.ATalkBodySendData;
import com.example.eaimessage.model.ATalkHeaderSendData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageSendRequest;
import com.example.eaimessage.model.MessageType;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class SmsMessageBuilder extends AbstractMessageBuilder {

    @Override
    protected String buildBodyString(MessageSendRequest request) {
        ATalkBodySendData body = new ATalkBodySendData();
        body.setSenderKey(defaultString(request.getSenderKey()));
        body.setRecipient(firstRecipient(request));
        applyMessageType(body, request);
        return body.toMessageString();
    }

    @Override
    protected String buildHeaderString(MessageSendRequest request, String bodyString) {
        ATalkHeaderSendData header = new ATalkHeaderSendData();
        header.setTransactionId(newTransactionId());
        header.setSenderSystemCode("EAI");
        header.setChannelCode(ChannelType.SMS.name());
        header.setMessageTypeCode(request.getMessageType().name());
        header.setBodyLength(bodyString.getBytes(StandardCharsets.UTF_8).length);
        return header.toFixedLengthString();
    }

    private void applyMessageType(ATalkBodySendData body, MessageSendRequest request) {
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
