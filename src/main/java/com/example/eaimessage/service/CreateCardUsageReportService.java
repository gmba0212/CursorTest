package com.example.eaimessage.service;

import com.example.eaimessage.mapper.PerformanceMapper;
import com.example.eaimessage.model.MgCardUsageReportRow;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CreateCardUsageReportService {

    private static final String COLUMN_DELIMITER = "|";
    private static final String ROW_DELIMITER = "@";
    private static final String TITLE = "MGCARD_USAGE_REPORT";
    private static final String HEADER = String.join(COLUMN_DELIMITER,
        "cardId", "cardName", "inventoryId", "registerUserId", "inventoryName", "cardTypeName",
        "displayWayName", "customerGroupTypeName", "prevViewCount", "prevClickCount", "prevCustomerCount",
        "prevClickCustomerCount", "prevClickRate", "totalViewCount", "totalClickCount", "totalCustomerCount",
        "totalClickCustomerCount", "totalClickRate", "cardRegisterDate", "firstViewDate", "lastViewDate");

    private final PerformanceMapper performanceMapper;

    public CreateCardUsageReportService(PerformanceMapper performanceMapper) {
        this.performanceMapper = performanceMapper;
    }

    public String createTitle() {
        return TITLE;
    }

    public String createContent(TalkRequest request) {
        List<MgCardUsageReportRow> rows = performanceMapper.selectCardUsageReportRows(safeUserId(request));
        if (rows == null || rows.isEmpty()) {
            return "NO_DATA";
        }

        String body = rows.stream()
            .map(this::toRow)
            .collect(Collectors.joining(ROW_DELIMITER));

        return HEADER + ROW_DELIMITER + body;
    }

    private String toRow(MgCardUsageReportRow row) {
        return Stream.of(
                row.getCardId(), row.getCardName(), row.getInventoryId(), row.getRegisterUserId(), row.getInventoryName(),
                row.getCardTypeName(), row.getDisplayWayName(), row.getCustomerGroupTypeName(), row.getPrevViewCount(),
                row.getPrevClickCount(), row.getPrevCustomerCount(), row.getPrevClickCustomerCount(), row.getPrevClickRate(),
                row.getTotalViewCount(), row.getTotalClickCount(), row.getTotalCustomerCount(), row.getTotalClickCustomerCount(),
                row.getTotalClickRate(), row.getCardRegisterDate(), row.getFirstViewDate(), row.getLastViewDate())
            .map(this::normalize)
            .collect(Collectors.joining(COLUMN_DELIMITER));
    }

    private String normalize(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value)
            .replace(COLUMN_DELIMITER, " ")
            .replace(ROW_DELIMITER, " ");
    }

    private String safeUserId(TalkRequest request) {
        if (request == null || request.getReceiverId() == null) {
            return "";
        }
        return request.getReceiverId().trim();
    }
}
