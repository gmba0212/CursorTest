package com.example.eaimessage.provider;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.PreparedMessageContent;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.OrderInfoService;
import org.springframework.stereotype.Component;

@Component
public class SettlementResultContentProvider implements MessageContentProvider {

    private final OrderInfoService orderInfoService;
    private final DefaultReceiverInfoResolver receiverInfoResolver;

    public SettlementResultContentProvider(
        OrderInfoService orderInfoService,
        DefaultReceiverInfoResolver receiverInfoResolver
    ) {
        this.orderInfoService = orderInfoService;
        this.receiverInfoResolver = receiverInfoResolver;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.SETTLEMENT_RESULT;
    }

    @Override
    public PreparedMessageContent provide(TalkRequest request) {
        String receiverId = safe(request.getReceiverId());
        String orderNo = "SETTLE-" + receiverId;
        String settlementStatus = orderInfoService.getSettlementResult(orderNo);

        return new PreparedMessageContent(
            "SETTLE_RES",
            receiverInfoResolver.resolveType(receiverId),
            receiverInfoResolver.resolveAddress(receiverId),
            receiverId,
            "[정산결과] " + orderNo,
            "정산 결과: " + settlementStatus
        );
    }

    private String safe(String receiverId) {
        return receiverId == null || receiverId.isBlank() ? "UNKNOWN" : receiverId.trim();
    }
}
