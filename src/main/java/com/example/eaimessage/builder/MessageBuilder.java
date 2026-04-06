package com.example.eaimessage.builder;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageSendRequest;
import com.example.eaimessage.model.MessagePayload;

public interface MessageBuilder {
    boolean supports(ChannelType channelType);
    MessagePayload build(MessageSendRequest request);
}
