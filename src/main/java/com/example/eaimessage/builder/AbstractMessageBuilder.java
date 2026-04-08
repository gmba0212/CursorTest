package com.example.eaimessage.builder;

import com.example.eaimessage.header.EaiHeaderFactory;
import com.example.eaimessage.header.FixedLengthFieldFormatter;
import com.example.eaimessage.model.MessagePayload;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.ExternalMessageDataService;

import java.nio.charset.StandardCharsets;

public abstract class AbstractMessageBuilder implements MessageBuilder {

    protected final EaiHeaderFactory headerFactory;
    private final ExternalMessageDataService externalMessageDataService;

    protected AbstractMessageBuilder(
        EaiHeaderFactory headerFactory,
        ExternalMessageDataService externalMessageDataService
    ) {
        this.headerFactory = headerFactory;
        this.externalMessageDataService = externalMessageDataService;
    }

    @Override
    public MessagePayload build(TalkRequest request) {
        if (request == null || request.getMessageType() == null || request.getChannelType() == null) {
            throw new IllegalArgumentException("request/channelType/messageType must not be null");
        }
        if (!supports(request.getChannelType(), request.getMessageType())) {
            throw new IllegalArgumentException("unsupported messageType/channelType");
        }
        ServiceData serviceData = externalMessageDataService.resolve(request);
        String body = buildBodyString(request, serviceData);
        String header = headerFactory.createHeader(
            newTransactionId(),
            request.getChannelType(),
            request.getMessageType(),
            utf8Length(body)
        );
        return new MessagePayload(header + body);
    }

    protected abstract String buildBodyString(TalkRequest request, ServiceData serviceData);

    protected String param(TalkRequest request, String key, String defaultValue) {
        if (request.getParams() == null) {
            return defaultValue;
        }
        Object value = request.getParams().get(key);
        if (value == null) {
            return defaultValue;
        }
        String casted = String.valueOf(value);
        return casted.isBlank() ? defaultValue : casted;
    }

    protected String defaultString(String value) {
        return value == null ? "" : value;
    }

    protected String fixed(String value, int length) {
        return FixedLengthFieldFormatter.rightPad(value, length);
    }

    protected String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    protected String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    protected String concat(String... parts) {
        StringBuilder sb = new StringBuilder();
        if (parts != null) {
            for (String part : parts) {
                sb.append(defaultString(part));
            }
        }
        return sb.toString();
    }

    private String newTransactionId() {
        return java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    protected int utf8Length(String value) {
        return defaultString(value).getBytes(StandardCharsets.UTF_8).length;
    }
}
