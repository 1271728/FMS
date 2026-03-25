package com.example.fms.modules.auth.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.modules.auth.dto.ChangePasswordReq;
import com.example.fms.modules.auth.dto.LoginReq;
import com.example.fms.modules.auth.dto.LoginResp;
import com.example.fms.modules.auth.dto.MeResp;
import com.example.fms.modules.auth.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResp> login(@RequestBody LoginReq req) {
        return ApiResponse.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.ok(null);
    }

    @GetMapping("/me")
    public ApiResponse<MeResp> me() {
        return ApiResponse.ok(authService.me());
    }

    @PostMapping("/changePassword")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordReq req) {
        authService.changePassword(req);
        return ApiResponse.ok(null);
    }
}
