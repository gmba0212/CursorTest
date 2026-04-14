package com.example.eaimessage.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 발송 요청 최소 입력 DTO.
 */
public class TalkRequest {

    private ChannelType channelType;
    private MessageType messageType;
    private String receiverId;
    private Map<String, Object> data = new HashMap<>();

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

    public Map<String, Object> getData() {
        return data == null ? Collections.emptyMap() : data;
    }

    public void setData(Map<String, Object> data) {
        this.data = (data == null) ? new HashMap<>() : new HashMap<>(data);
    }

    public Object get(String key) {
        return getData().get(key);
    }

    public String getString(String key) {
        Object value = getData().get(key);
        return value == null ? "" : String.valueOf(value);
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

        public Builder data(Map<String, Object> data) {
            request.setData(data);
            return this;
        }

        public Builder data(String key, Object value) {
            request.data.put(key, value);
            return this;
        }

        public TalkRequest build() {
            return request;
        }
    }
}
