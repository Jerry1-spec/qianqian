package com.report.common;

/**
 * 业务错误码，详见《技术设计补充说明》第4节。
 */
public enum ErrorCode {

    PARAM_INVALID(40001, "参数校验失败"),
    UNAUTHORIZED(40101, "未登录或登录已失效，请重新登录"),
    LOGIN_FAIL(40102, "账号或密码错误"),
    FORBIDDEN(40301, "无权限或当前状态不允许该操作"),
    CONFLICT(40901, "资源冲突"),
    SERVER_ERROR(50000, "服务器内部错误");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
