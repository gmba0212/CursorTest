package com.example.eaimessage.generator.body;

public record BodyData(
    String messageType,
    String receiverId,
    String title,
    String content
) {
}
