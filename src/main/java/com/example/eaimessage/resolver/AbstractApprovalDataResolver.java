package com.example.eaimessage.resolver;

import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.OrderInfoService;
import com.example.eaimessage.service.UserInfoService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 승인 요청/완료 타입이 동일한 외부 조회 로직을 공유한다.
 */
abstract class AbstractApprovalDataResolver extends AbstractResolverSupport {

    private final OrderInfoService orderInfoService;
    private final UserInfoService userInfoService;

    protected AbstractApprovalDataResolver(OrderInfoService orderInfoService, UserInfoService userInfoService) {
        this.orderInfoService = orderInfoService;
        this.userInfoService = userInfoService;
    }

    protected ServiceData resolveApproval(TalkRequest request) {
        String orderNo = param(request, "orderNo");
        String userId = request.getReceiverId() == null ? "" : request.getReceiverId();
        Map<String, Object> map = new HashMap<>();
        map.put("orderInfo", orderInfoService.getOrderInfo(orderNo));
        map.put("userInfo", userInfoService.getUserInfo(userId));
        map.put("documentNo", orderNo.isBlank() ? "DOC-UNKNOWN" : orderNo);
        map.put("approverName", request.getParams() == null ? "" : stringVal(request.getParams().get("approverName")));
        map.put("approveDate", LocalDate.now().toString());
        return new ServiceData(map);
    }
}
