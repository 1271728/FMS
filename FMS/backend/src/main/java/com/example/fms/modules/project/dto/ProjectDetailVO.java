package com.example.fms.modules.project.dto;

import java.util.List;

public class ProjectDetailVO extends ProjectVO {
    private String description;
    private String createdAt;
    private String updatedAt;
    private String unitAuditorName;
    private String unitAuditAt;
    private String unitAuditComment;
    private String financeAuditorName;
    private String financeAuditAt;
    private String financeAuditComment;
    private List<ProjectAuditLogVO> auditLogs;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

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

    public List<ProjectAuditLogVO> getAuditLogs() { return auditLogs; }
    public void setAuditLogs(List<ProjectAuditLogVO> auditLogs) { this.auditLogs = auditLogs; }
}
