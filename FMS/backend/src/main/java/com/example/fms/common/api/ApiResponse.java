package com.example.fms.common.api;

/**
 * 统一返回结构
 */
public class ApiResponse<T> {

    private int code;
    private boolean success;
    private String message;
    private T data;

    public ApiResponse() {}

    public ApiResponse(int code, boolean success, String message, T data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
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
}
