package com.example.fms.modules.archive.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.modules.archive.dto.ArchiveCreateReq;
import com.example.fms.modules.archive.dto.ArchiveVO;
import com.example.fms.modules.archive.service.ArchiveService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/archive")
public class ArchiveController {

    private final ArchiveService archiveService;

    public ArchiveController(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @PostMapping("/create")
    public ApiResponse<Void> create(@RequestBody ArchiveCreateReq req) {
        archiveService.create(req);
        return ApiResponse.ok(null);
    }

    @GetMapping("/detail")
    public ApiResponse<ArchiveVO> detail(@RequestParam("reimbId") Long reimbId) {
        return ApiResponse.ok(archiveService.detailByReimbId(reimbId));
    }
}
