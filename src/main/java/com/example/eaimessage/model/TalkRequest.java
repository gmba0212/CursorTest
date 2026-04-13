package com.example.eaimessage.model;

/**
 * 발송 요청 최소 입력 DTO.
 */
public class TalkRequest {

    private ChannelType channelType;
    private MessageType messageType;
    private String receiverId;

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TalkRequest request = new TalkRequest();

        public Builder channelType(ChannelType channelType) {
            request.setChannelType(channelType);
            return this;
        }

        public Builder messageType(MessageType messageType) {
            request.setMessageType(messageType);
            return this;
        }

        public Builder receiverId(String receiverId) {
            request.setReceiverId(receiverId);
            return this;
        }

        public TalkRequest build() {
            return request;
        }
    }
}
