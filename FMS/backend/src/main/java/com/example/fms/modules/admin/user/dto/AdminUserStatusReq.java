package com.example.fms.modules.admin.user.dto;

public class AdminUserStatusReq {
    private Long userId;
    private Integer status; // 1启用 0禁用

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
