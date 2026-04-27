package com.example.eaimessage.service.performance;

import com.example.eaimessage.mapper.PerformanceMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PerformanceService {

    private final PerformanceMapper performanceMapper;

    public PerformanceService(PerformanceMapper performanceMapper) {
        this.performanceMapper = performanceMapper;
    }

    public List<Map<String, Object>> getCardEventStatsByUserId(String userId) {
        return performanceMapper.selectCardEvtStatByUserId(userId);
    }
}
