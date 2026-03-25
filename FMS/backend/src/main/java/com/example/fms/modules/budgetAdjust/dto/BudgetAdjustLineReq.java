package com.example.fms.modules.budgetAdjust.dto;

import java.math.BigDecimal;

public class BudgetAdjustLineReq {
    private Long subjectId;
    private BigDecimal deltaAmount;
    private String remark;

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public BigDecimal getDeltaAmount() { return deltaAmount; }
    public void setDeltaAmount(BigDecimal deltaAmount) { this.deltaAmount = deltaAmount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
