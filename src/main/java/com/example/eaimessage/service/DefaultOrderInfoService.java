package com.example.eaimessage.service;

import org.springframework.stereotype.Service;

@Service
public class DefaultOrderInfoService implements OrderInfoService {
    @Override
    public String getOrderInfo(String orderNo) {
        String safeOrderNo = orderNo == null || orderNo.isBlank() ? "ORD-UNKNOWN" : orderNo;
        return "주문정보(" + safeOrderNo + ")";
    }

    @Override
    public String getSettlementResult(String orderNo) {
        String safeOrderNo = orderNo == null || orderNo.isBlank() ? "ORD-UNKNOWN" : orderNo;
        return "완료(" + safeOrderNo + ")";
    }
}
