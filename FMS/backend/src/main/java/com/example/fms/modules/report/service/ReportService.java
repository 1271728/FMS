package com.example.fms.modules.report.service;

import com.example.fms.modules.report.dto.ReimbStatReq;

import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Map<String, Object>> budgetExecution(Long projectId);
    Map<String, Object> reimbStat(ReimbStatReq req);
}
