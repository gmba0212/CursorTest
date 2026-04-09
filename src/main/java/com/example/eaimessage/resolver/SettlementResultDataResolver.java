package com.example.eaimessage.resolver;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.OrderInfoService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SettlementResultDataResolver extends AbstractResolverSupport implements MessageDataResolver {

    private final OrderInfoService orderInfoService;

    public SettlementResultDataResolver(OrderInfoService orderInfoService) {
        this.orderInfoService = orderInfoService;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.SETTLEMENT_RESULT;
    }

    @Override
    public ServiceData resolve(TalkRequest request) {
        String orderNo = param(request, "orderNo");
        Map<String, Object> map = new HashMap<>();
        map.put("settlementStatus", orderInfoService.getSettlementResult(orderNo));
        map.put("orderNo", orderNo);
        return new ServiceData(map);
    }
}
