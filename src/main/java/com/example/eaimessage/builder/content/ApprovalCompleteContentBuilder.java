package com.example.eaimessage.builder.content;

import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class ApprovalCompleteContentBuilder extends AbstractMessageContentSupport implements MessageContentBuilder {

    @Override
    public MessageType supportedType() {
        return MessageType.APPROVAL_COMPLETE;
    }

    @Override
    public MessageContent build(TalkRequest request, ServiceData serviceData) {
        return MessageContent.of(
            "APRV_DONE",
            "[승인완료] " + valueOr(serviceData, "approverName", "담당자"),
            "결재 완료 (" + valueOr(serviceData, "approveDate", "N/A") + ")"
        );
    }
}
