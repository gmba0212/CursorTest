package com.example.eaimessage.service.bodyfield;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class ApprovalRequestBodyFieldResolver implements MessageBodyFieldResolver {

    @Override
    public MessageType supportedType() {
        return MessageType.APPROVAL_REQUEST;
    }

    @Override
    public BodyDisplayFields resolve(TalkRequest request, MessageContext rawContext) {
        String approver = BodyFieldResolveSupport.valueOr(rawContext, "approverName", "담당자");
        String docNo = BodyFieldResolveSupport.valueOr(rawContext, "documentNo", "N/A");
        return BodyDisplayFields.of(
            "APRV_REQ",
            "[승인요청] " + approver,
            "결재 문서번호: " + docNo
        );
    }
}
