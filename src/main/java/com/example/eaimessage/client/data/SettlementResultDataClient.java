package com.example.eaimessage.client.data;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.OrderInfoService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SettlementResultDataClient implements MessageDataClient {

    private final OrderInfoService orderInfoService;

    public SettlementResultDataClient(OrderInfoService orderInfoService) {
        this.orderInfoService = orderInfoService;
    }

    @Override
    public MessageType supportedType() {
        return MessageType.SETTLEMENT_RESULT;
    }

    @Override
    public MessageContext fetch(TalkRequest request) {
        String orderNo = request.getContent() == null ? "" : request.getContent().trim();
        Map<String, Object> map = new HashMap<>();
        map.put("settlementStatus", orderInfoService.getSettlementResult(orderNo));
        map.put("orderNo", orderNo);
        return MessageContext.of(map);
    }
}
