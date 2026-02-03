package com.freshmart.irs.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiResponse", description = "统一响应体：{ code, message, data, traceId }")
public class ApiResponse<T> {
    @Schema(description = "业务码：0 表示成功；非 0 表示失败", example = "0")
    private int code;

    @Schema(description = "提示信息", example = "OK")
    private String message;

    @Schema(description = "响应数据，失败时为 null")
    private T data;

    @Schema(description = "链路追踪 ID，用于日志排障")
    private String traceId;

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.code = ErrorCode.OK.getCode();
        r.message = ErrorCode.OK.getDefaultMessage();
        r.data = data;
        r.traceId = TraceIdHolder.getOrCreate();
        return r;
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.code = errorCode.getCode();
        r.message = message == null || message.isBlank() ? errorCode.getDefaultMessage() : message;
        r.data = null;
        r.traceId = TraceIdHolder.getOrCreate();
        return r;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getTraceId() {
        return traceId;
    }
}

