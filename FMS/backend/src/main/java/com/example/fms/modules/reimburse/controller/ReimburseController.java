package com.example.fms.modules.reimburse.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.common.exception.BizException;
import com.example.fms.common.api.PageResult;
import com.example.fms.modules.reimburse.dto.*;
import com.example.fms.modules.shared.support.UserSupport;
import com.example.fms.modules.reimburse.service.ReimburseService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final UserSupport userSupport;

    public ReimburseController(ReimburseService reimburseService, UserSupport userSupport) {
        this.reimburseService = reimburseService;
        this.userSupport = userSupport;
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

    @GetMapping("/file/download")
    public ResponseEntity<Resource> download(@RequestParam("fileUrl") String fileUrl,
                                             @RequestParam(value = "name", required = false) String name) throws IOException {
        userSupport.currentUser();
        String normalized = StringUtils.cleanPath(fileUrl == null ? "" : fileUrl.trim());
        if (!normalized.startsWith("/uploads/reimburse/")) throw BizException.badRequest("文件路径非法");
        Path uploadsRoot = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();
        Path target = uploadsRoot.resolve(normalized.replaceFirst("^/uploads/", "")).normalize();
        if (!target.startsWith(uploadsRoot)) throw BizException.badRequest("文件路径非法");
        if (!Files.exists(target) || !Files.isRegularFile(target)) throw BizException.notFound("文件不存在");
        Resource resource = new UrlResource(target.toUri());
        String filename = StringUtils.hasText(name) ? name.trim() : target.getFileName().toString();
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        MediaType mediaType = MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }
}
