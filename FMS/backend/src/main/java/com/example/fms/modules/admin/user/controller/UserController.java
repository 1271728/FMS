package com.example.fms.modules.admin.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.fms.common.api.ApiResponse;
import com.example.fms.common.api.PageResult;
import com.example.fms.modules.admin.user.dto.*;
import com.example.fms.modules.admin.user.service.AdminUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AdminUserService adminUserService;

    public UserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    private void checkAdmin() {
        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");
    }

    @PostMapping("/page")
    public ApiResponse<PageResult<AdminUserVO>> page(@RequestBody AdminUserPageReq req) {
        checkAdmin();
        return ApiResponse.ok(adminUserService.page(req));
    }

    @PostMapping("/create")
    public ApiResponse<Map<String, Object>> create(@RequestBody AdminUserCreateReq req) {
        checkAdmin();
        Long id = adminUserService.create(req);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", id);
        return ApiResponse.ok(data);
    }

    @PostMapping("/update")
    public ApiResponse<Void> update(@RequestBody AdminUserUpdateReq req) {
        checkAdmin();
        adminUserService.update(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/disable")
    public ApiResponse<Void> disable(@RequestBody AdminUserStatusReq req) {
        checkAdmin();
        if (req != null && req.getStatus() == null) {
            req.setStatus(0);
        }
        adminUserService.setStatus(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/enable")
    public ApiResponse<Void> enable(@RequestBody AdminUserStatusReq req) {
        checkAdmin();
        if (req != null && req.getStatus() == null) {
            req.setStatus(1);
        }
        adminUserService.setStatus(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/resetPassword")
    public ApiResponse<Void> resetPassword(@RequestBody AdminUserResetPwdReq req) {
        checkAdmin();
        adminUserService.resetPassword(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/setRoles")
    public ApiResponse<Void> setRoles(@RequestBody AdminUserSetRolesReq req) {
        checkAdmin();
        adminUserService.setRoles(req);
        return ApiResponse.ok(null);
    }
}
