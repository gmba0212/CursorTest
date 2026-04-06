package com.example.eaimessage.factory;

import com.example.eaimessage.builder.ATalkMessageBuilder;
import com.example.eaimessage.builder.MailMessageBuilder;
import com.example.eaimessage.builder.MessageBuilder;
import com.example.eaimessage.builder.SmsMessageBuilder;
import com.example.eaimessage.model.ChannelType;
import org.springframework.stereotype.Component;

@Component
public class MessageBuilderFactory {

    private final ATalkMessageBuilder aTalkMessageBuilder;
    private final MailMessageBuilder mailMessageBuilder;
    private final SmsMessageBuilder smsMessageBuilder;

    public MessageBuilderFactory(
        ATalkMessageBuilder aTalkMessageBuilder,
        MailMessageBuilder mailMessageBuilder,
        SmsMessageBuilder smsMessageBuilder
    ) {
        this.aTalkMessageBuilder = aTalkMessageBuilder;
        this.mailMessageBuilder = mailMessageBuilder;
        this.smsMessageBuilder = smsMessageBuilder;
    }

    public MessageBuilder getBuilder(ChannelType channelType) {
        if (channelType == null) {
            throw new IllegalArgumentException("channelType must not be null");
        }
        return switch (channelType) {
            case ALIMTALK -> aTalkMessageBuilder;
            case MAIL -> mailMessageBuilder;
            case SMS -> smsMessageBuilder;
        };
    }
}
