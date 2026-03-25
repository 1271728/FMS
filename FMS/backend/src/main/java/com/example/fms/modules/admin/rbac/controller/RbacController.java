package com.example.fms.modules.admin.rbac.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.fms.common.api.ApiResponse;
import com.example.fms.modules.admin.user.mapper.RoleOption;
import com.example.fms.modules.admin.user.service.AdminUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色/权限字典（RBAC）
 *
 * 说明：
 * - 该接口用于前端下拉字典（如：管理员创建用户时选择角色）
 * - 与 /api/admin/user/roleOptions 逻辑一致，仅提供更通用的 REST 路径：/api/rbac/roles
 */
@RestController
@RequestMapping("/api/rbac")
public class RbacController {

    private final AdminUserService adminUserService;

    public RbacController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
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
}
