package com.example.eaimessage.builder.content;

import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class SettlementResultContentBuilder extends AbstractMessageContentSupport implements MessageContentBuilder {

    @Override
    public MessageType supportedType() {
        return MessageType.SETTLEMENT_RESULT;
    }

    @Override
    public MessageContent build(TalkRequest request, ServiceData serviceData) {
        return MessageContent.of(
            "SETTLE_RES",
            "[정산결과] " + valueOr(serviceData, "orderNo", "N/A"),
            "정산 결과: " + valueOr(serviceData, "settlementStatus", "UNKNOWN")
        );
    }
}
