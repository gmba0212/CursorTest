package com.example.eaimessage.service;

import com.example.eaimessage.service.performance.PerformanceService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MgCardUsageReportContentService {

    private static final String COLUMN_DELIMITER = "|";
    private static final String ROW_DELIMITER = "@";

    private final PerformanceService performanceService;

    public MgCardUsageReportContentService(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    public String getTitle(String receiverId) {
        return "MGCARD_USAGE_REPORT";
    }

    public String getContent(String receiverId) {
        List<Map<String, Object>> rows = performanceService.getCardEventStatsByUserId(safe(receiverId));
        if (rows == null || rows.isEmpty()) {
            return "NO_DATA";
        }

        Set<String> orderedColumns = extractOrderedColumns(rows);
        String headerLine = String.join(COLUMN_DELIMITER, orderedColumns);

        String dataLines = rows.stream()
            .map(row -> orderedColumns.stream()
                .map(column -> normalize(row.get(column)))
                .collect(Collectors.joining(COLUMN_DELIMITER)))
            .collect(Collectors.joining(ROW_DELIMITER));

        return headerLine + ROW_DELIMITER + dataLines;
    }

    private static Set<String> extractOrderedColumns(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> orderedColumns = new LinkedHashSet<>(rows.get(0).keySet());
        for (Map<String, Object> row : rows) {
            orderedColumns.addAll(row.keySet());
        }
        return orderedColumns;
    }

    private static String normalize(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        return text.replace(COLUMN_DELIMITER, " ")
            .replace(ROW_DELIMITER, " ");
    }

    private static String safe(String receiverId) {
        return receiverId == null ? "" : receiverId.trim();
    }
}
