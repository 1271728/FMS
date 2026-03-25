package com.example.fms.modules.budgetAdjust.service;

import com.example.fms.common.api.PageResult;
import com.example.fms.modules.budgetAdjust.dto.*;

public interface BudgetAdjustService {
    PageResult<BudgetAdjustVO> page(BudgetAdjustPageReq req);
    BudgetAdjustDetailVO detail(Long id);
    Long create(BudgetAdjustCreateReq req);
    void update(BudgetAdjustUpdateReq req);
    void delete(BudgetAdjustIdReq req);
    void submit(BudgetAdjustIdReq req);
    void withdraw(BudgetAdjustIdReq req);
    void workflowApprove(Long bizId, String nodeCode, String comment);
    void workflowReject(Long bizId, String nodeCode, String comment);
}
