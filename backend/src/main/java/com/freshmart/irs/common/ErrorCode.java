package com.freshmart.irs.common;

public enum ErrorCode {
    OK(0, "OK"),
    PARAM_INVALID(40001, "参数错误"),
    UNAUTHORIZED(40100, "未登录或令牌无效"),
    FORBIDDEN(40300, "无权限"),
    NOT_FOUND(40400, "资源不存在"),
    INTERNAL_ERROR(50000, "服务器异常");

    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

