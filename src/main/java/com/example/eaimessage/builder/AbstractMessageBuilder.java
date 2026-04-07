package com.example.eaimessage.builder;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessagePayload;
import com.example.eaimessage.model.MessageSendRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractMessageBuilder implements MessageBuilder {

    @Override
    public boolean supports(ChannelType channelType) {
        return channelType == channelType();
    }

    @Override
    public MessagePayload build(MessageSendRequest request) {
        validateRequest(request);
        String bodyString = buildBodyString(request);
        String headerString = buildHeaderString(request, bodyString);
        return new MessagePayload(headerString + bodyString);
    }

    protected abstract String buildBodyString(MessageSendRequest request);

    protected abstract String buildHeaderString(MessageSendRequest request, String bodyString);

    protected abstract ChannelType channelType();

    protected void validateRequest(MessageSendRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.getChannelType() != channelType()) {
            throw new IllegalArgumentException("unsupported channel type for builder: " + request.getChannelType());
        }
        if (request.getMessageType() == null) {
            throw new IllegalArgumentException("messageType must not be null");
        }
    }

    protected String defaultString(String value) {
        return value == null ? "" : value;
    }

    protected String firstRecipient(MessageSendRequest request) {
        if (request.getRecipients() == null || request.getRecipients().isEmpty()) {
            return "";
        }
        String first = request.getRecipients().get(0);
        return first == null ? "" : first;
    }

    protected String fromData(MessageSendRequest request, String key) {
        if (request.getData() == null) {
            return "";
        }
        Object value = request.getData().get(key);
        return value == null ? "" : String.valueOf(value);
    }

    protected String newTransactionId() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    protected int utf8Length(String value) {
        return defaultString(value).getBytes(StandardCharsets.UTF_8).length;
    }
}
