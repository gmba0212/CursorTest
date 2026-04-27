package com.example.eaimessage.mapper;

import com.example.eaimessage.model.MgCardUsageReportRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PerformanceMapper {

    List<MgCardUsageReportRow> selectCardUsageReportRows(@Param("userId") String userId);
}
