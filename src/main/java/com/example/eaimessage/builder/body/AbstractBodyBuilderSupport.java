package com.example.eaimessage.builder.body;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import com.example.eaimessage.config.KTalkProperties;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.TalkRequest;

abstract class AbstractBodyBuilderSupport {

    protected String valueOr(MessageContext data, String key, String defaultValue) {
        String v = data.getString(key);
        return v == null || v.isBlank() ? defaultValue : v;
    }

    protected String defaultString(String value) {
        return value == null ? "" : value;
    }

    protected String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return "";
    }

    protected String buildByChannel(TalkRequest request, KTalkProperties kTalkProperties, String templateCode, String subject, String content) {
        if (request.getChannelType() == ChannelType.KTALK) {
            return formatKtalk(defaultString(templateCode), defaultString(kTalkProperties.getSenderKey()),
                defaultString(request.getReceiverAddress()), defaultString(subject), defaultString(content));
        }
        if (request.getChannelType() == ChannelType.EMAIL) {
            return formatEmail(firstNonBlank(subject, request.getTitle(), "[메일]"),
                firstNonBlank(request.getReceiverType(), "USER"),
                defaultString(request.getReceiverAddress()), defaultString(request.getReceiverId()), defaultString(content));
        }
        return formatSms(ChannelType.SMS.name(), defaultString(request.getReceiverType()), defaultString(request.getReceiverAddress()),
            defaultString(request.getReceiverId()), firstNonBlank(subject, request.getTitle(), "알림"),
            firstNonBlank(content, request.getContent(), "SMS 안내 메시지입니다."));
    }

    private String formatKtalk(String templateCode, String senderKey, String recipient, String subject, String content) {
        return FixedLengthFieldFormatter.rightPad(templateCode, 10)
            + FixedLengthFieldFormatter.rightPad(senderKey, 20)
            + FixedLengthFieldFormatter.rightPad(recipient, 30)
            + FixedLengthFieldFormatter.rightPad(subject, 80)
            + FixedLengthFieldFormatter.rightPad(content, 300);
    }

    private String formatEmail(String title, String receiverType, String receiverAddress, String receiverId, String content) {
        return FixedLengthFieldFormatter.rightPad(title, 80)
            + FixedLengthFieldFormatter.rightPad(receiverType, 10)
            + FixedLengthFieldFormatter.rightPad(receiverAddress, 80)
            + FixedLengthFieldFormatter.rightPad(receiverId, 20)
            + FixedLengthFieldFormatter.rightPad(content, 400);
    }

    private String formatSms(String channel, String receiverType, String receiverAddress, String receiverId, String title, String content) {
        return FixedLengthFieldFormatter.rightPad(channel, 10)
            + FixedLengthFieldFormatter.rightPad(receiverType, 10)
            + FixedLengthFieldFormatter.rightPad(receiverAddress, 30)
            + FixedLengthFieldFormatter.rightPad(receiverId, 20)
            + FixedLengthFieldFormatter.rightPad(title, 80)
            + FixedLengthFieldFormatter.rightPad(content, 300);
    }
}
