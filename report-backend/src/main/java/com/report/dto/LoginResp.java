package com.report.dto;

/**
 * 登录响应，字段命名与 PRD/前端约定一致（need_change_pwd）。
 */
public class LoginResp {

    private String token;
    private String role;
    private Long userId;
    private boolean need_change_pwd;

    public LoginResp(String token, String role, Long userId, boolean needChangePwd) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.need_change_pwd = needChangePwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isNeed_change_pwd() {
        return need_change_pwd;
    }

    public void setNeed_change_pwd(boolean need_change_pwd) {
        this.need_change_pwd = need_change_pwd;
    }
}
