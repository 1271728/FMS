package com.example.fms.modules.admin.user.dto;

import java.util.List;

/**
 * 管理员创建用户（系统不开放自助注册）
 */
public class AdminUserCreateReq {
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Long unitId;
    private Integer status; // 1启用 0禁用（默认1）
    private List<String> roleCodes; // ADMIN/PI/UNIT_ADMIN/FINANCE

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<String> getRoleCodes() { return roleCodes; }
    public void setRoleCodes(List<String> roleCodes) { this.roleCodes = roleCodes; }
}
