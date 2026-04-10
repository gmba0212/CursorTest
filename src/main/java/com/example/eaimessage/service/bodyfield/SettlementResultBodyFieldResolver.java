package com.example.eaimessage.service.bodyfield;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class SettlementResultBodyFieldResolver implements MessageBodyFieldResolver {

    @Override
    public MessageType supportedType() {
        return MessageType.SETTLEMENT_RESULT;
    }

    @Override
    public BodyDisplayFields resolve(TalkRequest request, MessageContext rawContext) {
        String orderNo = BodyFieldResolveSupport.valueOr(rawContext, "orderNo", "N/A");
        String status = BodyFieldResolveSupport.valueOr(rawContext, "settlementStatus", "UNKNOWN");
        return BodyDisplayFields.of(
            "SETTLE_RES",
            "[정산결과] " + orderNo,
            "정산 결과: " + status
        );
    }
}
