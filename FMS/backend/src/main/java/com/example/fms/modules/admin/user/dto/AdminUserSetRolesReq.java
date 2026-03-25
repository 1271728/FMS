package com.example.fms.modules.admin.user.dto;

import java.util.List;

public class AdminUserSetRolesReq {
    private Long userId;
    private List<String> roleCodes;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<String> getRoleCodes() { return roleCodes; }
    public void setRoleCodes(List<String> roleCodes) { this.roleCodes = roleCodes; }
}
