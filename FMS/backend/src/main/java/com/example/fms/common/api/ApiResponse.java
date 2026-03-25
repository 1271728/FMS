package com.example.fms.common.api;

import com.example.fms.common.logging.TraceIdSupport;

/**
 * 统一返回结构
 */
public class ApiResponse<T> {

    private int code;
    private boolean success;
    private String message;
    private T data;
    private String traceId;

    public ApiResponse() {}

    public ApiResponse(int code, boolean success, String message, T data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
        this.traceId = TraceIdSupport.currentTraceId();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, true, "OK", data);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, false, message, null);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
}
