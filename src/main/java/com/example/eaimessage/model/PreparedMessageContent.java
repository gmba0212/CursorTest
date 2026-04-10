package com.example.eaimessage.model;

/**
 * messageType 전용 provider가 조회/조합을 끝낸 뒤 전달하는 본문 데이터.
 */
public record PreparedMessageContent(
    String templateCode,
    String receiverType,
    String receiverAddress,
    String receiverId,
    String subject,
    String content
) {
}
