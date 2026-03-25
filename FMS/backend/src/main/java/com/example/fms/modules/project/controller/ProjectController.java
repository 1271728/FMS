package com.example.fms.modules.project.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.common.api.PageResult;
import com.example.fms.modules.project.dto.*;
import com.example.fms.modules.project.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/page")
    public ApiResponse<PageResult<ProjectVO>> page(@RequestBody(required = false) ProjectPageReq req) {
        return ApiResponse.ok(projectService.page(req));
    }

    @GetMapping("/detail")
    public ApiResponse<ProjectDetailVO> detail(@RequestParam("id") Long id) {
        return ApiResponse.ok(projectService.detail(id));
    }

    @PostMapping("/create")
    public ApiResponse<Map<String, Object>> create(@RequestBody ProjectCreateReq req) {
        Long id = projectService.create(req);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", id);
        return ApiResponse.ok(data);
    }

    @PostMapping("/update")
    public ApiResponse<Void> update(@RequestBody ProjectUpdateReq req) {
        projectService.update(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestBody ProjectDeleteReq req) {
        projectService.delete(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/submit")
    public ApiResponse<Void> submit(@RequestBody ProjectSubmitReq req) {
        projectService.submit(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody ProjectWithdrawReq req) {
        projectService.withdraw(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/audit")
    public ApiResponse<Void> audit(@RequestBody ProjectAuditReq req) {
        projectService.audit(req);
        return ApiResponse.ok(null);
    }

    @GetMapping("/members")
    public ApiResponse<List<ProjectMemberVO>> members(@RequestParam("projectId") Long projectId) {
        return ApiResponse.ok(projectService.members(projectId));
    }

    @PostMapping("/addMember")
    public ApiResponse<Void> addMember(@RequestBody ProjectAddMemberReq req) {
        projectService.addMember(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/removeMember")
    public ApiResponse<Void> removeMember(@RequestBody ProjectRemoveMemberReq req) {
        projectService.removeMember(req);
        return ApiResponse.ok(null);
    }
}
