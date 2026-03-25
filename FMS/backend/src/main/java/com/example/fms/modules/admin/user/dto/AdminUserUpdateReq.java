package com.example.fms.modules.admin.user.dto;

public class AdminUserUpdateReq {
    private Long id;
    private String realName;
    private String phone;
    private String email;
    private Long unitId;
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
}
