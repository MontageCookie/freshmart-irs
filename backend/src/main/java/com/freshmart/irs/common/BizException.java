package com.freshmart.irs.common;

/**
 * 业务异常：用于承载业务错误码并由全局异常处理转换为统一响应体。
 */
public class BizException extends RuntimeException {
    private final ErrorCode errorCode;

    /**
     * 构造业务异常。
     *
     * @param errorCode 业务错误码
     * @param message   错误提示
     */
    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 获取业务错误码。
     *
     * @return 错误码
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
