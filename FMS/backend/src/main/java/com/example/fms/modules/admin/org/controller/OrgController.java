package com.example.fms.modules.admin.org.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.fms.common.api.ApiResponse;
import com.example.fms.modules.admin.org.dto.OrgTreeNode;
import com.example.fms.modules.admin.org.service.OrgService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/org")
public class OrgController {

    private final OrgService orgService;

    public OrgController(OrgService orgService) {
        this.orgService = orgService;
    }

    private void checkAdmin() {
        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");
    }

    @GetMapping("/tree")
    public ApiResponse<List<OrgTreeNode>> tree() {
        checkAdmin();
        return ApiResponse.ok(orgService.tree());
    }
}
