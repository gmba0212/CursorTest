package com.example.eaimessage.header;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import org.springframework.stereotype.Component;

@Component
public class EaiHeaderFactory {

    private final EaiHeaderGenerator eaiHeaderGenerator;

    public EaiHeaderFactory(EaiHeaderGenerator eaiHeaderGenerator) {
        this.eaiHeaderGenerator = eaiHeaderGenerator;
    }

    public String createHeader(
        String transactionId,
        ChannelType channelType,
        MessageType messageType,
        int bodyLength
    ) {
        return eaiHeaderGenerator.create(
            transactionId,
            channelType.name(),
            messageType.name(),
            bodyLength
        );
    }
}
