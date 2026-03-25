package com.example.fms.modules.auth.dto;

import java.time.LocalDateTime;
import java.util.List;

public class LoginResp {
    private String tokenValue;
    private UserInfo user;

    public LoginResp() {}

    public LoginResp(String tokenValue, UserInfo user) {
        this.tokenValue = tokenValue;
        this.user = user;
    }

    public String getTokenValue() { return tokenValue; }
    public void setTokenValue(String tokenValue) { this.tokenValue = tokenValue; }

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private Long unitId;
        private String unitName;
        private List<String> roles;
        private List<String> perms;
        private String phone;
        private String email;
        private Integer status;
        private LocalDateTime lastLoginAt;

        public UserInfo() {}

        public UserInfo(Long id, String username, String realName, Long unitId, String unitName,
                        List<String> roles, List<String> perms, String phone, String email,
                        Integer status, LocalDateTime lastLoginAt) {
            this.id = id;
            this.username = username;
            this.realName = realName;
            this.unitId = unitId;
            this.unitName = unitName;
            this.roles = roles;
            this.perms = perms;
            this.phone = phone;
            this.email = email;
            this.status = status;
            this.lastLoginAt = lastLoginAt;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }

        public Long getUnitId() { return unitId; }
        public void setUnitId(Long unitId) { this.unitId = unitId; }

        public String getUnitName() { return unitName; }
        public void setUnitName(String unitName) { this.unitName = unitName; }

        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }

        public List<String> getPerms() { return perms; }
        public void setPerms(List<String> perms) { this.perms = perms; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }

        public LocalDateTime getLastLoginAt() { return lastLoginAt; }
        public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    }
}
