package com.example.eaimessage.generator.header;

import com.example.eaimessage.model.MessageType;

public interface EaiHeaderGenerator {

    String generate(String transactionId, MessageType messageType, int bodyLength, String title, String content);
}
