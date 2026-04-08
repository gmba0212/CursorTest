package com.example.eaimessage.builder;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.MessagePayload;
import com.example.eaimessage.model.TalkRequest;

public interface MessageBuilder {
    boolean supports(ChannelType channelType, MessageType messageType);

    MessagePayload build(TalkRequest request);
}
