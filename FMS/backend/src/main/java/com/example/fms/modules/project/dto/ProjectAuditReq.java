package com.example.fms.modules.project.dto;

public class ProjectAuditReq {
    private Long projectId;
    private String action;
    private String comment;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
