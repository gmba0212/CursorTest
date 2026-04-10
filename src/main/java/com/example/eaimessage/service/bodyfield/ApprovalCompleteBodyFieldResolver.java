package com.example.eaimessage.service.bodyfield;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class ApprovalCompleteBodyFieldResolver implements MessageBodyFieldResolver {

    @Override
    public MessageType supportedType() {
        return MessageType.APPROVAL_COMPLETE;
    }

    @Override
    public BodyDisplayFields resolve(TalkRequest request, MessageContext rawContext) {
        String approver = BodyFieldResolveSupport.valueOr(rawContext, "approverName", "담당자");
        String date = BodyFieldResolveSupport.valueOr(rawContext, "approveDate", "N/A");
        return BodyDisplayFields.of(
            "APRV_DONE",
            "[승인완료] " + approver,
            "결재 완료 (" + date + ")"
        );
    }
}
