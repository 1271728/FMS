package com.example.fms.modules.admin.user.dto;

public class AdminUserResetPwdReq {
    private Long userId;
    private String newPassword; // 为空则重置为 123456

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
