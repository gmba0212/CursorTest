package com.example.eaimessage.client.data;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.OrderInfoService;
import com.example.eaimessage.service.UserInfoService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractApprovalDataClient {

    private final OrderInfoService orderInfoService;
    private final UserInfoService userInfoService;

    protected AbstractApprovalDataClient(OrderInfoService orderInfoService, UserInfoService userInfoService) {
        this.orderInfoService = orderInfoService;
        this.userInfoService = userInfoService;
    }

    protected MessageContext fetchApproval(TalkRequest request) {
        String orderNo = request.getContent() == null ? "" : request.getContent().trim();
        String userId = request.getReceiverId() == null ? "" : request.getReceiverId();
        Map<String, Object> map = new HashMap<>();
        map.put("orderInfo", orderInfoService.getOrderInfo(orderNo));
        map.put("userInfo", userInfoService.getUserInfo(userId));
        map.put("documentNo", orderNo.isBlank() ? "DOC-UNKNOWN" : orderNo);
        map.put("approverName", userInfoService.getDisplayName(userId));
        map.put("approveDate", LocalDate.now().toString());
        return MessageContext.of(map);
    }
}
