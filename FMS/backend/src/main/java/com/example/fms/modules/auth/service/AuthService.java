package com.example.fms.modules.auth.service;

import com.example.fms.modules.auth.dto.ChangePasswordReq;
import com.example.fms.modules.auth.dto.LoginReq;
import com.example.fms.modules.auth.dto.LoginResp;
import com.example.fms.modules.auth.dto.MeResp;

public interface AuthService {
    LoginResp login(LoginReq req);
    void logout();
    MeResp me();
    void changePassword(ChangePasswordReq req);
}
