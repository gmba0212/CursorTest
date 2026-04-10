package com.example.eaimessage.service;

import org.springframework.stereotype.Service;

@Service
public class DefaultOrderInfoService implements OrderInfoService {
    @Override
    public String findLatestOrderNoByUser(String userId) {
        String safeUserId = userId == null || userId.isBlank() ? "UNKNOWN" : userId.trim();
        return "ORD-" + safeUserId.toUpperCase();
    }

    @Override
    public String getSettlementResult(String orderNo) {
        String safeOrderNo = orderNo == null || orderNo.isBlank() ? "ORD-UNKNOWN" : orderNo;
        return "완료(" + safeOrderNo + ")";
    }
}
