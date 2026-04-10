package com.example.eaimessage.builder.body;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import com.example.eaimessage.config.KTalkProperties;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.TalkRequest;

abstract class AbstractBodyBuilderSupport {

    protected String buildByChannel(
        TalkRequest request,
        KTalkProperties kTalkProperties,
        String templateCode,
        String receiverType,
        String subject,
        String content
    ) {
        if (request.getChannelType() == ChannelType.KTALK) {
            return formatKtalk(templateCode, kTalkProperties.getSenderKey(), request.getReceiverAddress(), subject, content);
        }
        if (request.getChannelType() == ChannelType.EMAIL) {
            return formatEmail(subject, receiverType, request.getReceiverAddress(), request.getReceiverId(), content);
        }
        return formatSms(ChannelType.SMS.name(), receiverType, request.getReceiverAddress(), request.getReceiverId(), subject, content);
    }

    protected String formatKtalk(String templateCode, String senderKey, String recipient, String subject, String content) {
        return FixedLengthFieldFormatter.rightPad(templateCode, 10)
            + FixedLengthFieldFormatter.rightPad(senderKey, 20)
            + FixedLengthFieldFormatter.rightPad(recipient, 30)
            + FixedLengthFieldFormatter.rightPad(subject, 80)
            + FixedLengthFieldFormatter.rightPad(content, 300);
    }

    protected String formatEmail(String title, String receiverType, String receiverAddress, String receiverId, String content) {
        return FixedLengthFieldFormatter.rightPad(title, 80)
            + FixedLengthFieldFormatter.rightPad(receiverType, 10)
            + FixedLengthFieldFormatter.rightPad(receiverAddress, 80)
            + FixedLengthFieldFormatter.rightPad(receiverId, 20)
            + FixedLengthFieldFormatter.rightPad(content, 400);
    }

    protected String formatSms(String channel, String receiverType, String receiverAddress, String receiverId, String title, String content) {
        return FixedLengthFieldFormatter.rightPad(channel, 10)
            + FixedLengthFieldFormatter.rightPad(receiverType, 10)
            + FixedLengthFieldFormatter.rightPad(receiverAddress, 30)
            + FixedLengthFieldFormatter.rightPad(receiverId, 20)
            + FixedLengthFieldFormatter.rightPad(title, 80)
            + FixedLengthFieldFormatter.rightPad(content, 300);
    }
}
