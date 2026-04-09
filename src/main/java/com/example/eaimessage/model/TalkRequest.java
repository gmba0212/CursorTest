package com.example.eaimessage.model;

import java.util.Map;

/**
 * UMS/발송 요청 공통 DTO.
 * <p>
 * {@link #channelType}·{@link #messageType}은 반드시 이 필드로만 지정한다.
 * 모듈은 레지스트리 선택 시 {@code params}에서 채널/메시지 타입을 읽지 않는다.
 * {@code params}는 주문번호·URL·승인자명 등 업무 확장 데이터만 둔다.
 */
public class TalkRequest {
    private ChannelType channelType;
    private MessageType messageType;
    private String title;
    private String receiverType;
    private String receiverAddress;
    private String receiverId;
    private String content;
    private Map<String, Object> params;

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

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
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

        public Builder params(Map<String, Object> params) {
            request.setParams(params);
            return this;
        }

        public TalkRequest build() {
            return request;
        }
    }
}
