package com.example.fms.modules.budget.service;

import com.example.fms.modules.budget.dto.BudgetSubjectNode;
import com.example.fms.modules.budget.dto.ProjectBudgetVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BudgetService {
    List<BudgetSubjectNode> subjectTree();
    List<ProjectBudgetVO> projectBudgetList(Long projectId);
    void ensureProjectBudgetSubjects(Long projectId, BigDecimal totalBudget);
    void ensureDefaultBudgetLedger(Long projectId, BigDecimal totalBudget);
    Map<String, Object> backfillLegacyProjectBudgets();
}
