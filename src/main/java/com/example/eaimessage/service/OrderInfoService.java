package com.example.eaimessage.service;

public interface OrderInfoService {
    String getOrderInfo(String orderNo);
    String getSettlementResult(String orderNo);
}
