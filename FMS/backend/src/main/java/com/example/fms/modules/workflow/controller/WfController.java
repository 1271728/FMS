package com.example.fms.modules.workflow.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.common.api.PageResult;
import com.example.fms.modules.workflow.dto.*;
import com.example.fms.modules.workflow.service.WfService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wf")
public class WfController {

    private final WfService wfService;

    public WfController(WfService wfService) {
        this.wfService = wfService;
    }

    @PostMapping("/todoPage")
    public ApiResponse<PageResult<WfTaskVO>> todoPage(@RequestBody(required = false) WfTodoPageReq req) {
        return ApiResponse.ok(wfService.todoPage(req));
    }

    @PostMapping("/donePage")
    public ApiResponse<PageResult<WfTaskVO>> donePage(@RequestBody(required = false) WfDonePageReq req) {
        return ApiResponse.ok(wfService.donePage(req));
    }

    @PostMapping("/approve")
    public ApiResponse<Void> approve(@RequestBody WfActionReq req) {
        wfService.approve(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/reject")
    public ApiResponse<Void> reject(@RequestBody WfActionReq req) {
        wfService.reject(req);
        return ApiResponse.ok(null);
    }

    @GetMapping("/logs")
    public ApiResponse<List<WfLogVO>> logs(@RequestParam("bizType") String bizType, @RequestParam("bizId") Long bizId) {
        return ApiResponse.ok(wfService.logs(bizType, bizId));
    }
}
