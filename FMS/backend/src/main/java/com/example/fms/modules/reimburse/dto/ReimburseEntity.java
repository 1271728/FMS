package com.example.fms.modules.reimburse.dto;

import java.math.BigDecimal;

public class ReimburseEntity {
    private Long id;
    private String reimburseNo;
    private Long projectId;
    private Long applicantUserId;
    private Long unitId;
    private String title;
    private BigDecimal totalAmount;
    private Integer status;
    private String currentNode;
    private String description;
    private String submittedAt;
    private Long unitAuditorUserId;
    private String unitAuditAt;
    private String unitAuditComment;
    private Long financeAuditorUserId;
    private String financeAuditAt;
    private String financeAuditComment;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReimburseNo() { return reimburseNo; }
    public void setReimburseNo(String reimburseNo) { this.reimburseNo = reimburseNo; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getApplicantUserId() { return applicantUserId; }
    public void setApplicantUserId(Long applicantUserId) { this.applicantUserId = applicantUserId; }
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCurrentNode() { return currentNode; }
    public void setCurrentNode(String currentNode) { this.currentNode = currentNode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    public Long getUnitAuditorUserId() { return unitAuditorUserId; }
    public void setUnitAuditorUserId(Long unitAuditorUserId) { this.unitAuditorUserId = unitAuditorUserId; }
    public String getUnitAuditAt() { return unitAuditAt; }
    public void setUnitAuditAt(String unitAuditAt) { this.unitAuditAt = unitAuditAt; }
    public String getUnitAuditComment() { return unitAuditComment; }
    public void setUnitAuditComment(String unitAuditComment) { this.unitAuditComment = unitAuditComment; }
    public Long getFinanceAuditorUserId() { return financeAuditorUserId; }
    public void setFinanceAuditorUserId(Long financeAuditorUserId) { this.financeAuditorUserId = financeAuditorUserId; }
    public String getFinanceAuditAt() { return financeAuditAt; }
    public void setFinanceAuditAt(String financeAuditAt) { this.financeAuditAt = financeAuditAt; }
    public String getFinanceAuditComment() { return financeAuditComment; }
    public void setFinanceAuditComment(String financeAuditComment) { this.financeAuditComment = financeAuditComment; }
}
