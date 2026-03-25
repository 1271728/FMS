package com.example.fms.modules.workflow.handler;

import com.example.fms.modules.budgetAdjust.service.BudgetAdjustService;
import com.example.fms.modules.workflow.dto.WfBizTypes;
import org.springframework.stereotype.Component;

@Component
public class BudgetAdjustWfBizHandler implements WfBizHandler {

    private final BudgetAdjustService budgetAdjustService;

    public BudgetAdjustWfBizHandler(BudgetAdjustService budgetAdjustService) {
        this.budgetAdjustService = budgetAdjustService;
    }

    @Override
    public String bizType() {
        return WfBizTypes.BUDGET_ADJUST;
    }

    @Override
    public void approve(Long bizId, String nodeCode, String comment) {
        budgetAdjustService.workflowApprove(bizId, nodeCode, comment);
    }

    @Override
    public void reject(Long bizId, String nodeCode, String comment) {
        budgetAdjustService.workflowReject(bizId, nodeCode, comment);
    }
}
