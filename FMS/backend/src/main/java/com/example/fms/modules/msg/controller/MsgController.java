package com.example.fms.modules.msg.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.common.api.PageResult;
import com.example.fms.modules.msg.dto.*;
import com.example.fms.modules.msg.service.MsgService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/msg")
public class MsgController {

    private final MsgService msgService;

    public MsgController(MsgService msgService) {
        this.msgService = msgService;
    }

    @PostMapping("/page")
    public ApiResponse<PageResult<MsgVO>> page(@RequestBody(required = false) MsgPageReq req) {
        return ApiResponse.ok(msgService.page(req));
    }

    @GetMapping("/unreadCount")
    public ApiResponse<Integer> unreadCount() {
        return ApiResponse.ok(msgService.unreadCount());
    }

    @PostMapping("/markRead")
    public ApiResponse<Void> markRead(@RequestBody MsgMarkReadReq req) {
        msgService.markRead(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/markReadBatch")
    public ApiResponse<Void> markReadBatch(@RequestBody MsgMarkReadBatchReq req) {
        msgService.markReadBatch(req);
        return ApiResponse.ok(null);
    }
}
