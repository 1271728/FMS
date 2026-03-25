package com.example.fms.modules.shared.support;

import cn.dev33.satoken.stp.StpUtil;
import com.example.fms.common.exception.BizException;
import com.example.fms.modules.auth.entity.SysUser;
import com.example.fms.modules.auth.mapper.RbacMapper;
import com.example.fms.modules.auth.mapper.SysUserMapper;
import com.example.fms.modules.project.dto.ProjectEntity;
import com.example.fms.modules.project.mapper.ProjectMemberMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class UserSupport {

    private final SysUserMapper sysUserMapper;
    private final RbacMapper rbacMapper;
    private final ProjectMemberMapper projectMemberMapper;

    public UserSupport(SysUserMapper sysUserMapper, RbacMapper rbacMapper, ProjectMemberMapper projectMemberMapper) {
        this.sysUserMapper = sysUserMapper;
        this.rbacMapper = rbacMapper;
        this.projectMemberMapper = projectMemberMapper;
    }

    public CurrentUser currentUser() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw BizException.unauthorized("用户不存在");
        return new CurrentUser(user, normalizeRoleCodes(rbacMapper.selectRoleCodesByUserId(userId)));
    }

    public List<String> normalizeRoleCodes(List<String> roles) {
        LinkedHashSet<String> normalized = new LinkedHashSet<String>();
        if (roles == null) return new ArrayList<String>(normalized);
        for (String role : roles) {
            if (role == null) continue;
            String v = role.trim().toUpperCase();
            if (!v.isEmpty()) normalized.add(v);
        }
        return new ArrayList<String>(normalized);
    }

    public boolean hasAdminRole(List<String> roles) { return hasAnyRole(roles, "ADMIN"); }
    public boolean hasTeacherRole(List<String> roles) { return hasAnyRole(roles, "PI"); }
    public boolean hasUnitAdminRole(List<String> roles) { return hasAnyRole(roles, "UNIT_ADMIN"); }
    public boolean hasFinanceRole(List<String> roles) { return hasAnyRole(roles, "FINANCE"); }

    public boolean hasAnyRole(List<String> roles, String... candidates) {
        if (roles == null || roles.isEmpty()) return false;
        List<String> normalized = normalizeRoleCodes(roles);
        for (String role : normalized) {
            for (String c : candidates) {
                if (role.equals(c)) return true;
            }
        }
        return false;
    }

    public List<String> accessCodes(List<String> roles) {
        List<String> normalized = normalizeRoleCodes(roles);
        LinkedHashSet<String> codes = new LinkedHashSet<String>();
        if (hasAnyRole(normalized, "PI", "ADMIN", "UNIT_ADMIN", "FINANCE")) {
            codes.add("HOME");
            codes.add("PROJECT_MANAGE");
            codes.add("BUDGET_OVERVIEW");
            codes.add("BUDGET_ADJUST");
            codes.add("REIMBURSE_MANAGE");
            codes.add("MSG_CENTER");
        }
        if (hasAdminRole(normalized)) codes.add("ADMIN_USERS");
        if (hasAnyRole(normalized, "ADMIN", "UNIT_ADMIN", "FINANCE")) codes.add("WORKFLOW_CENTER");
        return new ArrayList<String>(codes);
    }

    public boolean canReadProject(ProjectEntity project, CurrentUser cu) {
        if (project == null || cu == null) return false;
        if (hasAdminRole(cu.getRoles()) || hasFinanceRole(cu.getRoles())) return true;
        if (hasUnitAdminRole(cu.getRoles())) {
            return cu.getUser().getUnitId() != null && cu.getUser().getUnitId().equals(project.getUnitId());
        }
        if (hasTeacherRole(cu.getRoles())) {
            return (project.getPrincipalUserId() != null && project.getPrincipalUserId().equals(cu.getUser().getId()))
                    || projectMemberMapper.countByProjectIdAndUserId(project.getId(), cu.getUser().getId()) > 0;
        }
        return false;
    }

    public static class CurrentUser {
        private final SysUser user;
        private final List<String> roles;

        public CurrentUser(SysUser user, List<String> roles) {
            this.user = user;
            this.roles = roles;
        }

        public SysUser getUser() { return user; }
        public List<String> getRoles() { return roles; }
    }
}
