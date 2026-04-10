package com.example.eaimessage.provider;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.PreparedMessageContent;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.OrderInfoService;
import com.example.eaimessage.service.UserInfoService;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class ApprovalCompleteContentProvider implements MessageContentProvider {

    private final OrderInfoService orderInfoService;
    private final UserInfoService userInfoService;
    private final DefaultReceiverInfoResolver receiverInfoResolver;

    public ApprovalCompleteContentProvider(
        OrderInfoService orderInfoService,
        UserInfoService userInfoService,
        DefaultReceiverInfoResolver receiverInfoResolver
    ) {
        this.orderInfoService = orderInfoService;
        this.userInfoService = userInfoService;
        this.receiverInfoResolver = receiverInfoResolver;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.APPROVAL_COMPLETE;
    }

    @Override
    public PreparedMessageContent provide(TalkRequest request) {
        String receiverId = safe(request.getReceiverId());
        String documentNo = orderInfoService.findLatestOrderNoByUser(receiverId);
        String resolvedDocumentNo = documentNo.isBlank() ? "DOC-UNKNOWN" : documentNo;
        String approverName = userInfoService.getDisplayName(receiverId);

        return new PreparedMessageContent(
            "APRV_DONE",
            receiverInfoResolver.resolveType(receiverId),
            receiverInfoResolver.resolveAddress(receiverId),
            receiverId,
            "[승인완료] " + approverName,
            "결재 완료 (" + LocalDate.now() + ") - 문서번호: " + resolvedDocumentNo
        );
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
