package com.example.eaimessage.model;

/**
 * Body generator가 문자열 포맷팅만 수행하도록, 메시지 내용을 사전 계산해 전달한다.
 */
public record BodyGenerationInput(
    ChannelType channelType,
    MessageType messageType,
    String receiverType,
    String receiverAddress,
    String receiverId,
    String templateCode,
    String senderKey,
    String subject,
    String content
) {
}
