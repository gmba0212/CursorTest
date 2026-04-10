package com.example.eaimessage.provider;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.PreparedMessageContent;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.OrderInfoService;
import org.springframework.stereotype.Component;

@Component
public class ApprovalRequestContentProvider implements MessageContentProvider {

    private final OrderInfoService orderInfoService;
    private final DefaultReceiverInfoResolver receiverInfoResolver;

    public ApprovalRequestContentProvider(
        OrderInfoService orderInfoService,
        DefaultReceiverInfoResolver receiverInfoResolver
    ) {
        this.orderInfoService = orderInfoService;
        this.receiverInfoResolver = receiverInfoResolver;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.APPROVAL_REQUEST;
    }

    @Override
    public PreparedMessageContent provide(TalkRequest request) {
        String receiverId = safe(request.getReceiverId());
        String documentNo = orderInfoService.findLatestOrderNoByUser(receiverId);
        String resolvedDocumentNo = documentNo.isBlank() ? "DOC-UNKNOWN" : documentNo;
        return new PreparedMessageContent(
            "APRV_REQ",
            receiverInfoResolver.resolveType(receiverId),
            receiverInfoResolver.resolveAddress(receiverId),
            receiverId,
            "[승인요청] " + receiverInfoResolver.resolveDisplayName(receiverId),
            "결재 문서번호: " + resolvedDocumentNo
        );
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
