package com.example.eaimessage.model;

/**
 * UMS/발송 요청 공통 DTO.
 * <p>
 * 채널·메시지 종류는 {@link #channelType}, {@link #messageType}만 사용한다.
 * 업무 식별자·원문 URL 등은 {@link #content}, {@link #title} 등 명시 필드에 둔다.
 * 범용 key-value 맵은 사용하지 않는다.
 */
public class TalkRequest {

    private ChannelType channelType;
    private MessageType messageType;
    private String title;
    private String receiverType;
    private String receiverAddress;
    private String receiverId;
    private String content;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

        public Builder title(String title) {
            request.setTitle(title);
            return this;
        }

        public Builder receiverType(String receiverType) {
            request.setReceiverType(receiverType);
            return this;
        }

        public Builder receiverAddress(String receiverAddress) {
            request.setReceiverAddress(receiverAddress);
            return this;
        }

        public Builder receiverId(String receiverId) {
            request.setReceiverId(receiverId);
            return this;
        }

        public Builder content(String content) {
            request.setContent(content);
            return this;
        }

        public TalkRequest build() {
            return request;
        }
    }
}
