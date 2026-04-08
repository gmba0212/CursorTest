package com.example.eaimessage.builder;

import com.example.eaimessage.header.EaiHeaderFactory;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.EmailBodyPayload;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.ExternalMessageDataService;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageBuilder extends AbstractMessageBuilder {

    public EmailMessageBuilder(
        EaiHeaderFactory eaiHeaderFactory,
        ExternalMessageDataService externalMessageDataService
    ) {
        super(eaiHeaderFactory, externalMessageDataService);
    }

    @Override
    public boolean supports(ChannelType channelType, MessageType messageType) {
        return channelType == ChannelType.EMAIL;
    }

    @Override
    protected String buildBodyString(TalkRequest request, ServiceData serviceData) {
        EmailBodyPayload payload = new EmailBodyPayload(
            firstNonBlank(request.getTitle(), serviceData.getString("title"), "[메일]"),
            firstNonBlank(request.getReceiverType(), "USER"),
            firstNonBlank(request.getReceiverAddress(), serviceData.getString("receiverAddress"), ""),
            firstNonBlank(request.getReceiverId(), serviceData.getString("receiverId"), ""),
            firstNonBlank(request.getContent(), serviceData.getString("content"), "")
        );
        return payload.buildMessage();
    }
}
