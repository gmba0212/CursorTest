package com.example.eaimessage.model;

import com.example.eaimessage.header.FixedLengthFieldFormatter;

public class SmsBodyPayload {
    private static final int CHANNEL_LEN = 10;
    private static final int RECEIVER_TYPE_LEN = 10;
    private static final int RECEIVER_ADDRESS_LEN = 30;
    private static final int RECEIVER_ID_LEN = 20;
    private static final int TITLE_LEN = 80;
    private static final int CONTENT_LEN = 300;

    private final String channel;
    private final String receiverType;
    private final String receiverAddress;
    private final String receiverId;
    private final String title;
    private final String content;

    public SmsBodyPayload(
        String channel,
        String receiverType,
        String receiverAddress,
        String receiverId,
        String title,
        String content
    ) {
        this.channel = FixedLengthFieldFormatter.rightPad(channel, CHANNEL_LEN);
        this.receiverType = FixedLengthFieldFormatter.rightPad(receiverType, RECEIVER_TYPE_LEN);
        this.receiverAddress = FixedLengthFieldFormatter.rightPad(receiverAddress, RECEIVER_ADDRESS_LEN);
        this.receiverId = FixedLengthFieldFormatter.rightPad(receiverId, RECEIVER_ID_LEN);
        this.title = FixedLengthFieldFormatter.rightPad(title, TITLE_LEN);
        this.content = FixedLengthFieldFormatter.rightPad(content, CONTENT_LEN);
    }

    public String buildMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(channel);
        sb.append(receiverType);
        sb.append(receiverAddress);
        sb.append(receiverId);
        sb.append(title);
        sb.append(content);
        return sb.toString();
    }
}
