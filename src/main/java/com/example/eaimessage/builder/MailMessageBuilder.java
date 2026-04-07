package com.example.eaimessage.builder;

import com.example.eaimessage.model.ATalkBodySendData;
import com.example.eaimessage.model.ATalkHeaderSendData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageSendRequest;
import com.example.eaimessage.model.MessageType;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class MailMessageBuilder extends AbstractMessageBuilder {

    @Override
    protected String buildBodyString(MessageSendRequest request) {
        ATalkBodySendData body = new ATalkBodySendData();
        body.setTemplateCode(defaultString(request.getTemplateCode()));
        body.setSenderKey(defaultString(request.getSenderKey()));
        body.setRecipient(firstRecipient(request));
        body.setSubject(defaultString(request.getSubject()));
        body.setContent(defaultString(request.getContent()));

        applyMessageType(body, request);
        return body.toMessageString();
    }

    @Override
    protected String buildHeaderString(MessageSendRequest request, String bodyString) {
        ATalkHeaderSendData header = new ATalkHeaderSendData();
        header.setTransactionId(newTransactionId());
        header.setSenderSystemCode("EAI");
        header.setChannelCode(ChannelType.MAIL.name());
        header.setMessageTypeCode(request.getMessageType().name());
        header.setBodyLength(bodyString.getBytes(StandardCharsets.UTF_8).length);
        return header.toFixedLengthString();
    }

    @Override
    protected void applyMessageType(ATalkBodySendData body, MessageSendRequest request) {
        MessageType messageType = request.getMessageType();
        switch (messageType) {
            case MAIL_PERFORMANCE_REPORT -> applyPerformanceReport(body, request);
            case MAIL_NOTICE -> applyMailNotice(body, request);
            default -> throw new IllegalArgumentException("MAIL 채널 미지원 messageType: " + messageType);
        }
    }

    @Override
    protected ChannelType channelType() {
        return ChannelType.MAIL;
    }

    private void applyPerformanceReport(ATalkBodySendData body, MessageSendRequest request) {
        body.setTemplateCode("MAIL_REP");
        body.setSubject(defaultString(request.getSubject()).isBlank() ? "[성능리포트] 정기 보고" : request.getSubject());
        body.setContent(defaultString(request.getContent()).isBlank() ? "성능 리포트가 도착했습니다." : request.getContent());
    }

    private void applyMailNotice(ATalkBodySendData body, MessageSendRequest request) {
        body.setTemplateCode(defaultString(request.getTemplateCode()).isBlank() ? "MAIL_NOTI" : request.getTemplateCode());
        body.setSubject(defaultString(request.getSubject()).isBlank() ? "[안내] 공지" : request.getSubject());
        body.setContent(defaultString(request.getContent()).isBlank() ? "공지사항을 확인해주세요." : request.getContent());
    }
}
