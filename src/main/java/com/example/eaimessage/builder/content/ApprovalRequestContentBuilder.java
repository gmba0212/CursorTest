package com.example.eaimessage.builder.content;

import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class ApprovalRequestContentBuilder extends AbstractMessageContentSupport implements MessageContentBuilder {

    @Override
    public MessageType supportedType() {
        return MessageType.APPROVAL_REQUEST;
    }

    @Override
    public MessageContent build(TalkRequest request, ServiceData serviceData) {
        return MessageContent.of(
            "APRV_REQ",
            "[승인요청] " + valueOr(serviceData, "approverName", "담당자"),
            "결재 문서번호: " + valueOr(serviceData, "documentNo", "N/A")
        );
    }
}
