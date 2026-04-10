package com.example.eaimessage.generator.header;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;

public interface MessageHeaderGenerator {

    String generate(String transactionId, ChannelType channelType, MessageType messageType, int bodyLength);
}
