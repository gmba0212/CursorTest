package com.example.eaimessage.builder.header;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;

public interface HeaderBuilder {

    String build(String transactionId, ChannelType channelType, MessageType messageType, int bodyLength);
}
