package com.example.fms.modules.budgetAdjust.dto;

import java.math.BigDecimal;

public class BudgetAdjustVO {
    private Long id;
    private String adjustNo;
    private Long projectId;
    private String projectCode;
    private String projectName;
    private Long applicantId;
    private String applicantName;
    private Long unitId;
    private String unitName;
    private String reason;
    private BigDecimal totalDelta;
    private Integer status;
    private String submittedAt;
    private Integer canEdit;
    private Integer canSubmit;
    private Integer canWithdraw;
    private Integer canUnitAudit;
    private Integer canFinanceAudit;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAdjustNo() { return adjustNo; }
    public void setAdjustNo(String adjustNo) { this.adjustNo = adjustNo; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public BigDecimal getTotalDelta() { return totalDelta; }
    public void setTotalDelta(BigDecimal totalDelta) { this.totalDelta = totalDelta; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    public Integer getCanEdit() { return canEdit; }
    public void setCanEdit(Integer canEdit) { this.canEdit = canEdit; }
    public Integer getCanSubmit() { return canSubmit; }
    public void setCanSubmit(Integer canSubmit) { this.canSubmit = canSubmit; }
    public Integer getCanWithdraw() { return canWithdraw; }
    public void setCanWithdraw(Integer canWithdraw) { this.canWithdraw = canWithdraw; }
    public Integer getCanUnitAudit() { return canUnitAudit; }
    public void setCanUnitAudit(Integer canUnitAudit) { this.canUnitAudit = canUnitAudit; }
    public Integer getCanFinanceAudit() { return canFinanceAudit; }
    public void setCanFinanceAudit(Integer canFinanceAudit) { this.canFinanceAudit = canFinanceAudit; }
}
