package com.example.eaimessage.builder;

import com.example.eaimessage.header.EaiHeaderFactory;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.SmsBodyPayload;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.ExternalMessageDataService;
import org.springframework.stereotype.Component;

@Component
public class SmsMessageBuilder extends AbstractMessageBuilder {

    public SmsMessageBuilder(
        EaiHeaderFactory eaiHeaderFactory,
        ExternalMessageDataService externalMessageDataService
    ) {
        super(eaiHeaderFactory, externalMessageDataService);
    }

    @Override
    public boolean supports(ChannelType channelType, MessageType messageType) {
        return channelType == ChannelType.SMS
            && (messageType == MessageType.AUTH_CODE || messageType == MessageType.SIMPLE_NOTICE);
    }

    @Override
    protected String buildBodyString(TalkRequest request, ServiceData serviceData) {
        String title = firstNonBlank(request.getTitle(), "알림");
        String content = firstNonBlank(request.getContent(), "SMS 안내 메시지입니다.");
        if (request.getMessageType() == MessageType.AUTH_CODE) {
            title = "[인증번호]";
            content = "인증번호는 [" + firstNonBlank(serviceData.getString("authCode"), "000000") + "] 입니다.";
        }

        SmsBodyPayload payload = new SmsBodyPayload(
            defaultString(request.getReceiverType()),
            defaultString(request.getReceiverAddress()),
            defaultString(request.getReceiverId()),
            title,
            content
        );
        return payload.buildMessage();
    }
}
