package com.example.eaimessage.model;

import com.example.eaimessage.header.FixedLengthFieldFormatter;

public class EmailBodyPayload {
    private static final int TITLE_LEN = 80;
    private static final int RECEIVER_TYPE_LEN = 10;
    private static final int RECEIVER_ADDRESS_LEN = 80;
    private static final int RECEIVER_ID_LEN = 20;
    private static final int CONTENT_LEN = 400;

    private final String title;
    private final String receiverType;
    private final String receiverAddress;
    private final String receiverId;
    private final String content;

    public EmailBodyPayload(
        String title,
        String receiverType,
        String receiverAddress,
        String receiverId,
        String content
    ) {
        this.title = title;
        this.receiverType = receiverType;
        this.receiverAddress = receiverAddress;
        this.receiverId = receiverId;
        this.content = content;
    }

    public String buildMessage() {
        return FixedLengthFieldFormatter.rightPad(title, TITLE_LEN)
            + FixedLengthFieldFormatter.rightPad(receiverType, RECEIVER_TYPE_LEN)
            + FixedLengthFieldFormatter.rightPad(receiverAddress, RECEIVER_ADDRESS_LEN)
            + FixedLengthFieldFormatter.rightPad(receiverId, RECEIVER_ID_LEN)
            + FixedLengthFieldFormatter.rightPad(content, CONTENT_LEN);
    }
}
