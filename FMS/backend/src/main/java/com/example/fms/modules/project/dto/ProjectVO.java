package com.example.fms.modules.project.dto;

import java.math.BigDecimal;

public class ProjectVO {
    private Long id;
    private String projectCode;
    private String projectName;
    private String projectType;
    private Long principalUserId;
    private String principalName;
    private Long unitId;
    private String unitName;
    private String startDate;
    private String endDate;
    private BigDecimal totalBudget;
    private Integer status;
    private Integer memberCount;
    private Integer canEdit;
    private Integer canSubmit;
    private Integer canManageMembers;
    private Integer canUnitAudit;
    private Integer canFinanceAudit;
    private Integer canWithdraw;
    private String submittedAt;

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

    public String getPrincipalName() { return principalName; }
    public void setPrincipalName(String principalName) { this.principalName = principalName; }

    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }

    public Integer getCanEdit() { return canEdit; }
    public void setCanEdit(Integer canEdit) { this.canEdit = canEdit; }

    public Integer getCanSubmit() { return canSubmit; }
    public void setCanSubmit(Integer canSubmit) { this.canSubmit = canSubmit; }

    public Integer getCanManageMembers() { return canManageMembers; }
    public void setCanManageMembers(Integer canManageMembers) { this.canManageMembers = canManageMembers; }

    public Integer getCanUnitAudit() { return canUnitAudit; }
    public void setCanUnitAudit(Integer canUnitAudit) { this.canUnitAudit = canUnitAudit; }

    public Integer getCanFinanceAudit() { return canFinanceAudit; }
    public void setCanFinanceAudit(Integer canFinanceAudit) { this.canFinanceAudit = canFinanceAudit; }

    public Integer getCanWithdraw() { return canWithdraw; }
    public void setCanWithdraw(Integer canWithdraw) { this.canWithdraw = canWithdraw; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
}
