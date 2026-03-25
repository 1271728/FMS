package com.example.fms.modules.budget.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.modules.budget.dto.BudgetSubjectNode;
import com.example.fms.modules.budget.dto.ProjectBudgetVO;
import com.example.fms.modules.budget.service.BudgetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/subject/tree")
    public ApiResponse<List<BudgetSubjectNode>> subjectTree() {
        return ApiResponse.ok(budgetService.subjectTree());
    }

    @GetMapping("/project/list")
    public ApiResponse<List<ProjectBudgetVO>> projectBudgetList(@RequestParam("projectId") Long projectId) {
        return ApiResponse.ok(budgetService.projectBudgetList(projectId));
    }

    @PostMapping("/project/backfill-all")
    public ApiResponse<Map<String, Object>> backfillAllProjectBudgets() {
        return ApiResponse.ok(budgetService.backfillLegacyProjectBudgets());
    }
}
