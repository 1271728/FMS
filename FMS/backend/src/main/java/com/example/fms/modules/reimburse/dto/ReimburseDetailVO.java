package com.example.fms.modules.reimburse.dto;

import com.example.fms.modules.archive.dto.ArchiveVO;
import com.example.fms.modules.pay.dto.PaymentVO;

import java.math.BigDecimal;
import java.util.List;

public class ReimburseDetailVO {
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
    private String description;
    private String createdAt;
    private String updatedAt;
    private String submittedAt;
    private String unitAuditorName;
    private String unitAuditAt;
    private String unitAuditComment;
    private String financeAuditorName;
    private String financeAuditAt;
    private String financeAuditComment;
    private Integer canEdit;
    private Integer canSubmit;
    private Integer canLeaderAudit;
    private Integer canUnitAudit;
    private Integer canFinanceAudit;
    private Integer canWithdraw;
    private List<ReimburseItemVO> items;
    private List<ReimburseAuditLogVO> auditLogs;
    private List<ReimburseAttachmentVO> attachments;
    private PaymentVO payment;
    private ArchiveVO archive;

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
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    public String getUnitAuditorName() { return unitAuditorName; }
    public void setUnitAuditorName(String unitAuditorName) { this.unitAuditorName = unitAuditorName; }
    public String getUnitAuditAt() { return unitAuditAt; }
    public void setUnitAuditAt(String unitAuditAt) { this.unitAuditAt = unitAuditAt; }
    public String getUnitAuditComment() { return unitAuditComment; }
    public void setUnitAuditComment(String unitAuditComment) { this.unitAuditComment = unitAuditComment; }
    public String getFinanceAuditorName() { return financeAuditorName; }
    public void setFinanceAuditorName(String financeAuditorName) { this.financeAuditorName = financeAuditorName; }
    public String getFinanceAuditAt() { return financeAuditAt; }
    public void setFinanceAuditAt(String financeAuditAt) { this.financeAuditAt = financeAuditAt; }
    public String getFinanceAuditComment() { return financeAuditComment; }
    public void setFinanceAuditComment(String financeAuditComment) { this.financeAuditComment = financeAuditComment; }
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
    public List<ReimburseItemVO> getItems() { return items; }
    public void setItems(List<ReimburseItemVO> items) { this.items = items; }
    public List<ReimburseAuditLogVO> getAuditLogs() { return auditLogs; }
    public void setAuditLogs(List<ReimburseAuditLogVO> auditLogs) { this.auditLogs = auditLogs; }
    public List<ReimburseAttachmentVO> getAttachments() { return attachments; }
    public void setAttachments(List<ReimburseAttachmentVO> attachments) { this.attachments = attachments; }
    public PaymentVO getPayment() { return payment; }
    public void setPayment(PaymentVO payment) { this.payment = payment; }
    public ArchiveVO getArchive() { return archive; }
    public void setArchive(ArchiveVO archive) { this.archive = archive; }
}
