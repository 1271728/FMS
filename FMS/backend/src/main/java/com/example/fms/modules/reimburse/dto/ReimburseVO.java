package com.example.fms.modules.reimburse.dto;

import java.math.BigDecimal;

public class ReimburseVO {
    private Long id;
    private String reimburseNo;
    private Long projectId;
    private String projectCode;
    private String projectName;
    private Long applicantUserId;
    private String applicantName;
    private Long unitId;
    private String unitName;
    private String title;
    private BigDecimal totalAmount;
    private Integer status;
    private Integer itemCount;
    private String submittedAt;
    private Integer canEdit;
    private Integer canSubmit;
    private Integer canLeaderAudit;
    private Integer canUnitAudit;
    private Integer canFinanceAudit;
    private Integer canWithdraw;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReimburseNo() { return reimburseNo; }
    public void setReimburseNo(String reimburseNo) { this.reimburseNo = reimburseNo; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Long getApplicantUserId() { return applicantUserId; }
    public void setApplicantUserId(Long applicantUserId) { this.applicantUserId = applicantUserId; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    public Integer getCanEdit() { return canEdit; }
    public void setCanEdit(Integer canEdit) { this.canEdit = canEdit; }
    public Integer getCanSubmit() { return canSubmit; }
    public void setCanSubmit(Integer canSubmit) { this.canSubmit = canSubmit; }
    public Integer getCanLeaderAudit() { return canLeaderAudit; }
    public void setCanLeaderAudit(Integer canLeaderAudit) { this.canLeaderAudit = canLeaderAudit; }
    public Integer getCanUnitAudit() { return canUnitAudit; }
    public void setCanUnitAudit(Integer canUnitAudit) { this.canUnitAudit = canUnitAudit; }
    public Integer getCanFinanceAudit() { return canFinanceAudit; }
    public void setCanFinanceAudit(Integer canFinanceAudit) { this.canFinanceAudit = canFinanceAudit; }
    public Integer getCanWithdraw() { return canWithdraw; }
    public void setCanWithdraw(Integer canWithdraw) { this.canWithdraw = canWithdraw; }
}
