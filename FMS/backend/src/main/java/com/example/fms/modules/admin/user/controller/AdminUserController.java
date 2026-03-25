package com.example.fms.modules.admin.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.fms.common.api.ApiResponse;
import com.example.fms.common.api.PageResult;
import com.example.fms.modules.admin.user.dto.*;
import com.example.fms.modules.admin.user.mapper.RoleOption;
import com.example.fms.modules.admin.user.mapper.UnitOption;
import com.example.fms.modules.admin.user.service.AdminUserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员：用户与角色管理
 *
 * 说明：系统默认不开放自助注册，账号由管理员创建并分配角色。
 */
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    private void checkAdmin() {
        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");
    }

    /** 创建用户 */
    @PostMapping("/create")
    public ApiResponse<Map<String, Object>> create(@RequestBody AdminUserCreateReq req) {
        checkAdmin();
        Long id = adminUserService.create(req);
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return ApiResponse.ok(data);
    }

    /** 用户分页 */
    @PostMapping("/page")
    public ApiResponse<PageResult<AdminUserVO>> page(@RequestBody AdminUserPageReq req) {
        checkAdmin();
        return ApiResponse.ok(adminUserService.page(req));
    }

    /** 设置角色 */
    @PostMapping("/setRoles")
    public ApiResponse<Void> setRoles(@RequestBody AdminUserSetRolesReq req) {
        checkAdmin();
        adminUserService.setRoles(req);
        return ApiResponse.ok(null);
    }

    /** 启用/禁用 */
    @PostMapping("/setStatus")
    public ApiResponse<Void> setStatus(@RequestBody AdminUserStatusReq req) {
        checkAdmin();
        adminUserService.setStatus(req);
        return ApiResponse.ok(null);
    }

    /** 重置密码（默认 123456） */
    @PostMapping("/resetPassword")
    public ApiResponse<Void> resetPassword(@RequestBody AdminUserResetPwdReq req) {
        checkAdmin();
        adminUserService.resetPassword(req);
        return ApiResponse.ok(null);
    }

    /** 角色下拉 */
    @GetMapping("/roleOptions")
    public ApiResponse<List<RoleOption>> roleOptions() {
        checkAdmin();
        return ApiResponse.ok(adminUserService.roleOptions());
    }

    /** 单位下拉 */
    @GetMapping("/unitOptions")
    public ApiResponse<List<UnitOption>> unitOptions() {
        checkAdmin();
        return ApiResponse.ok(adminUserService.unitOptions());
    }
}
