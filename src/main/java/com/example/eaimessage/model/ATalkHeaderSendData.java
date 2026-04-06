package com.example.eaimessage.model;

public class ATalkHeaderSendData {
    private String transactionId;
    private String senderSystemCode;
    private String channelCode;
    private String messageTypeCode;
    private int bodyLength;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSenderSystemCode() {
        return senderSystemCode;
    }

    public void setSenderSystemCode(String senderSystemCode) {
        this.senderSystemCode = senderSystemCode;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getMessageTypeCode() {
        return messageTypeCode;
    }

    public void setMessageTypeCode(String messageTypeCode) {
        this.messageTypeCode = messageTypeCode;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public String toFixedLengthString() {
        String tx = rightPad(trimToLength(transactionId, 20), 20);
        String sender = rightPad(trimToLength(senderSystemCode, 10), 10);
        String channel = rightPad(trimToLength(channelCode, 10), 10);
        String type = rightPad(trimToLength(messageTypeCode, 30), 30);
        String length = String.format("%08d", Math.max(bodyLength, 0));
        return tx + sender + channel + type + length;
    }

    private String trimToLength(String value, int maxLength) {
        String safe = value == null ? "" : value;
        return safe.length() > maxLength ? safe.substring(0, maxLength) : safe;
    }

    private String rightPad(String value, int targetLength) {
        if (value.length() >= targetLength) {
            return value;
        }
        StringBuilder sb = new StringBuilder(value);
        while (sb.length() < targetLength) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
