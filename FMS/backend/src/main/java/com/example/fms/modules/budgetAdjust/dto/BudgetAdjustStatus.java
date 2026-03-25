package com.example.fms.modules.budgetAdjust.dto;

public interface BudgetAdjustStatus {
    int DRAFT = 0;
    int PENDING_UNIT = 1;
    int PENDING_FINANCE = 2;
    int EFFECTIVE = 3;
    int REJECTED = 4;
    int WITHDRAWN = 5;
}
