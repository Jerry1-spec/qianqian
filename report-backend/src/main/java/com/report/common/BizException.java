package com.report.common;

/**
 * 业务异常：携带错误码，由全局异常处理器转成统一响应体。
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public BizException(ErrorCode errorCode, String customMsg) {
        super(customMsg);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
