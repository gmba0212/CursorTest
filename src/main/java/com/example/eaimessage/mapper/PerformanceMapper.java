package com.example.eaimessage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PerformanceMapper {

    List<Map<String, Object>> selectCardEvtStatByUserId(@Param("userId") String userId);
}
