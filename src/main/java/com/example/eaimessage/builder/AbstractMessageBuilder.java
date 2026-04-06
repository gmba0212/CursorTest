package com.example.eaimessage.builder;

import com.example.eaimessage.model.ATalkBodySendData;
import com.example.eaimessage.model.ATalkHeaderSendData;
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
        validate(request);

        ATalkBodySendData body = new ATalkBodySendData();
        applyCommonBody(body, request);
        applyMessageType(body, request);

        String bodyString = body.toMessageString();

        ATalkHeaderSendData header = new ATalkHeaderSendData();
        applyHeader(header, request, bodyString);

        String payload = header.toFixedLengthString() + bodyString;
        return new MessagePayload(payload);
    }

    protected void applyCommonBody(ATalkBodySendData body, MessageSendRequest request) {
        body.setTemplateCode(defaultString(request.getTemplateCode()));
        body.setSenderKey(defaultString(request.getSenderKey()));
        body.setSubject(defaultString(request.getSubject()));
        body.setContent(defaultString(request.getContent()));
        body.setRecipient(firstRecipient(request));
    }

    protected void applyHeader(ATalkHeaderSendData header, MessageSendRequest request, String bodyString) {
        header.setTransactionId(generateTransactionId());
        header.setSenderSystemCode("EAI");
        header.setChannelCode(request.getChannelType().name());
        header.setMessageTypeCode(request.getMessageType().name());
        header.setBodyLength(bodyString.getBytes(StandardCharsets.UTF_8).length);
    }

    protected abstract void applyMessageType(ATalkBodySendData body, MessageSendRequest request);

    protected abstract ChannelType channelType();

    private void validate(MessageSendRequest request) {
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

    private String generateTransactionId() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}
