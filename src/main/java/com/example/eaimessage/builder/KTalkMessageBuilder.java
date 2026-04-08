package com.example.eaimessage.builder;

import com.example.eaimessage.header.EaiHeaderFactory;
import com.example.eaimessage.model.ATalkBodySendData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.ExternalMessageDataService;
import org.springframework.stereotype.Component;

@Component
public class KTalkMessageBuilder extends AbstractMessageBuilder {
    private static final MessageType[] SUPPORTED = {
        MessageType.APPROVAL_REQUEST,
        MessageType.APPROVAL_COMPLETE,
        MessageType.AUTH_CODE,
        MessageType.SIMPLE_NOTICE,
        MessageType.SETTLEMENT_RESULT,
        MessageType.SHORT_URL
    };

    public KTalkMessageBuilder(
        EaiHeaderFactory headerFactory,
        ExternalMessageDataService externalMessageDataService
    ) {
        super(headerFactory, externalMessageDataService);
    }

    @Override
    public boolean supports(ChannelType channelType, MessageType messageType) {
        if (channelType != ChannelType.KTALK || messageType == null) {
            return false;
        }
        for (MessageType type : SUPPORTED) {
            if (type == messageType) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected String buildBodyString(TalkRequest request, ServiceData serviceData) {
        ATalkBodySendData body = new ATalkBodySendData();
        body.setSenderKey(param(request, "senderKey", "DEFAULT_KTALK_KEY"));
        body.setRecipient(defaultString(request.getReceiverAddress()));
        switch (request.getMessageType()) {
            case APPROVAL_REQUEST -> {
                body.setTemplateCode("APRV_REQ");
                body.setSubject("[승인요청] " + valueOr(serviceData, "approverName", "담당자"));
                body.setContent("결재 문서번호: " + valueOr(serviceData, "documentNo", "N/A"));
            }
            case APPROVAL_COMPLETE -> {
                body.setTemplateCode("APRV_DONE");
                body.setSubject("[승인완료] " + valueOr(serviceData, "approverName", "담당자"));
                body.setContent("결재 완료 (" + valueOr(serviceData, "approveDate", "N/A") + ")");
            }
            case AUTH_CODE -> {
                body.setTemplateCode("AUTH_CODE");
                body.setSubject("[인증번호]");
                body.setContent("인증번호는 [" + valueOr(serviceData, "authCode", "000000") + "] 입니다.");
            }
            case SIMPLE_NOTICE -> {
                body.setTemplateCode(param(request, "templateCode", "KTALK_NOTICE"));
                body.setSubject(defaultString(request.getTitle()));
                body.setContent(defaultString(request.getContent()));
            }
            case SHORT_URL -> {
                body.setTemplateCode("SHORT_URL");
                body.setSubject(firstNonBlank(request.getTitle(), "[단축URL 안내]"));
                body.setContent("단축 URL: " + valueOr(serviceData, "shortUrl", "N/A"));
            }
            case SETTLEMENT_RESULT -> {
                body.setTemplateCode("SETTLE_RES");
                body.setSubject("[정산결과] " + valueOr(serviceData, "orderNo", "N/A"));
                body.setContent("정산 결과: " + valueOr(serviceData, "settlementStatus", "UNKNOWN"));
            }
            default -> throw new IllegalArgumentException("KTALK 미지원 messageType: " + request.getMessageType());
        }
        return body.buildMessage();
    }

    private String valueOr(ServiceData serviceData, String key, String defaultValue) {
        String value = serviceData.getString(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
