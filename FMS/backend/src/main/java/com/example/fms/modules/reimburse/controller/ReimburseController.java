package com.example.fms.modules.reimburse.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.common.api.PageResult;
import com.example.fms.modules.reimburse.dto.*;
import com.example.fms.modules.reimburse.service.ReimburseService;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping({"/api/reimburse", "/api/reimb"})
public class ReimburseController {

    private final ReimburseService reimburseService;

    public ReimburseController(ReimburseService reimburseService) {
        this.reimburseService = reimburseService;
    }

    @PostMapping("/page")
    public ApiResponse<PageResult<ReimburseVO>> page(@RequestBody(required = false) ReimbursePageReq req) {
        return ApiResponse.ok(reimburseService.page(req));
    }

    @GetMapping("/detail")
    public ApiResponse<ReimburseDetailVO> detail(@RequestParam("id") Long id) {
        return ApiResponse.ok(reimburseService.detail(id));
    }

    @PostMapping("/create")
    public ApiResponse<Long> create(@RequestBody ReimburseCreateReq req) {
        return ApiResponse.ok(reimburseService.create(req));
    }

    @PostMapping("/update")
    public ApiResponse<Void> update(@RequestBody ReimburseUpdateReq req) {
        reimburseService.update(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/submit")
    public ApiResponse<Void> submit(@RequestBody ReimburseSubmitReq req) {
        reimburseService.submit(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody ReimburseSubmitReq req) {
        reimburseService.withdraw(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/audit")
    public ApiResponse<Void> audit(@RequestBody ReimburseAuditReq req) {
        reimburseService.audit(req);
        return ApiResponse.ok(null);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ReimburseUploadVO> upload(@RequestParam("file") MultipartFile file,
                                                 @RequestParam(value = "fileCategory", required = false) String fileCategory) throws IOException {
        if (file == null || file.isEmpty()) return ApiResponse.fail(400, "请选择文件");
        String dateDir = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Path root = Paths.get(System.getProperty("user.dir"), "uploads", "reimburse", dateDir);
        Files.createDirectories(root);
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx >= 0) ext = original.substring(idx);
        String storage = UUID.randomUUID().toString().replace("-", "") + ext;
        Path dest = root.resolve(storage);
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        ReimburseUploadVO vo = new ReimburseUploadVO();
        vo.setFileCategory(fileCategory == null ? "OTHER" : fileCategory.trim().toUpperCase());
        vo.setOriginalName(original);
        vo.setStorageName(storage);
        vo.setFileUrl("/uploads/reimburse/" + dateDir + "/" + storage);
        vo.setFileSize(file.getSize());
        return ApiResponse.ok(vo);
    }
}
