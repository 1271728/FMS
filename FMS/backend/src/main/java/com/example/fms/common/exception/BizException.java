package com.example.fms.common.exception;

/**
 * 业务异常：带 code
 */
public class BizException extends RuntimeException {
    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static BizException badRequest(String msg) { return new BizException(400, msg); }
    public static BizException unauthorized(String msg) { return new BizException(401, msg); }
    public static BizException forbidden(String msg) { return new BizException(403, msg); }
    public static BizException notFound(String msg) { return new BizException(404, msg); }
    public static BizException conflict(String msg) { return new BizException(409, msg); }
    public static BizException serverError(String msg) { return new BizException(500, msg); }
}
