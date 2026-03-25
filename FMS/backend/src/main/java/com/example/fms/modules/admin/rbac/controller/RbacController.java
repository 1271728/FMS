package com.example.fms.modules.admin.rbac.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.fms.common.api.ApiResponse;
import com.example.fms.modules.admin.user.mapper.RoleOption;
import com.example.fms.modules.admin.user.service.AdminUserService;
import com.example.fms.modules.shared.support.UserSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色/权限字典（RBAC）
 */
@RestController
@RequestMapping("/api/rbac")
public class RbacController {

    private final AdminUserService adminUserService;
    private final UserSupport userSupport;

    public RbacController(AdminUserService adminUserService, UserSupport userSupport) {
        this.adminUserService = adminUserService;
        this.userSupport = userSupport;
    }

    private void checkAdmin() {
        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");
    }

    /**
     * GET /api/rbac/roles（ADMIN）
     * 返回：[{ roleCode, roleName }]
     */
    @GetMapping("/roles")
    public ApiResponse<List<RoleOption>> roles() {
        checkAdmin();
        return ApiResponse.ok(adminUserService.roleOptions());
    }

    /**
     * GET /api/rbac/access-codes（已登录）
     * 返回：当前用户可访问的前端能力码，例如 HOME/ADMIN_USERS/WORKFLOW_CENTER
     */
    @GetMapping("/access-codes")
    public ApiResponse<List<String>> accessCodes() {
        UserSupport.CurrentUser cu = userSupport.currentUser();
        return ApiResponse.ok(userSupport.accessCodes(cu.getRoles()));
    }
}
