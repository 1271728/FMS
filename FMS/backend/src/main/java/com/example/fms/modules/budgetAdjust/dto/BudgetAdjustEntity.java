package com.example.fms.modules.budgetAdjust.dto;

import java.math.BigDecimal;

public class BudgetAdjustEntity {
    private Long id;
    private String adjustNo;
    private Long projectId;
    private Long applicantId;
    private Long unitId;
    private String reason;
    private BigDecimal totalDelta;
    private Integer status;
    private String currentNode;
    private String lastComment;
    private String submittedAt;
    private String effectiveAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAdjustNo() { return adjustNo; }
    public void setAdjustNo(String adjustNo) { this.adjustNo = adjustNo; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public BigDecimal getTotalDelta() { return totalDelta; }
    public void setTotalDelta(BigDecimal totalDelta) { this.totalDelta = totalDelta; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCurrentNode() { return currentNode; }
    public void setCurrentNode(String currentNode) { this.currentNode = currentNode; }
    public String getLastComment() { return lastComment; }
    public void setLastComment(String lastComment) { this.lastComment = lastComment; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    public String getEffectiveAt() { return effectiveAt; }
    public void setEffectiveAt(String effectiveAt) { this.effectiveAt = effectiveAt; }
}
