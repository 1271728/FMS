package com.example.fms.modules.project.dto;

import java.math.BigDecimal;

public class ProjectEntity {
    private Long id;
    private String projectCode;
    private String projectName;
    private String projectType;
    private Long principalUserId;
    private Long unitId;
    private String startDate;
    private String endDate;
    private BigDecimal totalBudget;
    private Integer status;
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

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }

    public Long getPrincipalUserId() { return principalUserId; }
    public void setPrincipalUserId(Long principalUserId) { this.principalUserId = principalUserId; }

    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

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
