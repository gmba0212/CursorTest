package com.example.eaimessage.generator.header;

public record HeaderData(
    String systemCode,
    String interfaceId,
    String transactionId,
    String channelType,
    String messageType,
    int bodyLength,
    String title,
    String content
) {
}
