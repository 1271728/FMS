package com.example.fms.modules.project.dto;

import java.math.BigDecimal;

public class ProjectBudgetLineReq {
    private Long subjectId;
    private BigDecimal approvedAmount;

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
}
