package com.example.eaimessage.service;

public interface OrderInfoService {
    String findLatestOrderNoByUser(String userId);
    String getSettlementResult(String orderNo);
}
