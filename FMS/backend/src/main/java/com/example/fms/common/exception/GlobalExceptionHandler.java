package com.example.fms.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.example.fms.common.api.ApiResponse;
import com.example.fms.common.logging.TraceIdSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotLoginException.class)
    public ApiResponse<Void> handleNotLogin(NotLoginException e) {
        log.warn("NotLogin: {}", e.getMessage());
        return ApiResponse.fail(401, "未登录或登录已过期");
    }

    @ExceptionHandler(NotRoleException.class)
    public ApiResponse<Void> handleNotRole(NotRoleException e) {
        log.warn("NotRole: {}", e.getMessage());
        return ApiResponse.fail(403, "无角色权限");
    }

    @ExceptionHandler(NotPermissionException.class)
    public ApiResponse<Void> handleNotPerm(NotPermissionException e) {
        log.warn("NotPermission: {}", e.getMessage());
        return ApiResponse.fail(403, "无权限");
    }

    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBiz(BizException e) {
        log.warn("BizException(code={}): {}", e.getCode(), e.getMessage());
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception e) {
        String traceId = TraceIdSupport.currentTraceId();
        log.error("Unhandled exception, traceId={}", traceId, e);
        String message = traceId == null || traceId.isEmpty()
                ? "系统内部错误，请联系管理员"
                : "系统内部错误，请联系管理员（traceId=" + traceId + "）";
        return ApiResponse.fail(500, message);
    }
}
