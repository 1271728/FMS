package com.example.fms.modules.project.dto;

public class ProjectRemoveMemberReq {
    private Long projectId;
    private Long userId;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
