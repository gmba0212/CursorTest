package com.example.eaimessage.builder;

import com.example.eaimessage.model.ATalkBodySendData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageSendRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class ATalkMessageBuilder extends AbstractMessageBuilder {

    private static final String DEFAULT_TEMPLATE = "ATALK_INFO";

    @Override
    protected void applyMessageType(ATalkBodySendData body, MessageSendRequest request) {
        switch (request.getMessageType()) {
            case ATALK_APPROVAL_REQUEST -> applyApprovalRequest(body, request);
            case ATALK_APPROVAL_COMPLETE -> applyApprovalComplete(body, request);
            case ATALK_INFO -> applyInfo(body, request);
            default -> throw new IllegalArgumentException(
                "ALIMTALK에서 지원하지 않는 messageType: " + request.getMessageType());
        }
    }

    @Override
    protected ChannelType channelType() {
        return ChannelType.ALIMTALK;
    }

    private void applyApprovalRequest(ATalkBodySendData body, MessageSendRequest request) {
        String approverName = fromData(request, "approverName");
        String documentNo = fromData(request, "documentNo");

        body.setTemplateCode("APRV_REQ");
        body.setSubject("[승인요청] " + (approverName.isBlank() ? "담당자" : approverName));
        body.setContent("결재 문서번호: " + (documentNo.isBlank() ? "N/A" : documentNo));
    }

    private void applyApprovalComplete(ATalkBodySendData body, MessageSendRequest request) {
        String approverName = fromData(request, "approverName");
        String approveDate = fromData(request, "approveDate");
        if (approveDate.isBlank()) {
            approveDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        }

        body.setTemplateCode("APRV_DONE");
        body.setSubject("[승인완료] " + (approverName.isBlank() ? "담당자" : approverName));
        body.setContent("결재 완료 (" + approveDate + ")");
    }

    private void applyInfo(ATalkBodySendData body, MessageSendRequest request) {
        body.setTemplateCode(request.getTemplateCode() == null || request.getTemplateCode().isBlank()
            ? DEFAULT_TEMPLATE : request.getTemplateCode());
        body.setSubject(defaultString(request.getSubject()));
        body.setContent(defaultString(request.getContent()));
    }
}
