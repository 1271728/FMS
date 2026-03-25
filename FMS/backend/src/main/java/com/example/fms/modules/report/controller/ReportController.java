package com.example.fms.modules.report.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.modules.report.dto.ReimbStatReq;
import com.example.fms.modules.report.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/budgetExecution")
    public ApiResponse<List<Map<String, Object>>> budgetExecution(@RequestParam("projectId") Long projectId) {
        return ApiResponse.ok(reportService.budgetExecution(projectId));
    }

    @PostMapping("/reimbStat")
    public ApiResponse<Map<String, Object>> reimbStat(@RequestBody(required = false) ReimbStatReq req) {
        return ApiResponse.ok(reportService.reimbStat(req));
    }
}
