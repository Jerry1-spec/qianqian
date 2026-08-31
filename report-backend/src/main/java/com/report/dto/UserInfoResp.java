package com.report.dto;

/**
 * 当前登录用户信息（GET /api/user/info）。
 */
public class UserInfoResp {

    private Long userId;
    private String username;
    private String role;
    private boolean needChangePwd;

    public UserInfoResp(Long userId, String username, String role, boolean needChangePwd) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.needChangePwd = needChangePwd;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isNeedChangePwd() {
        return needChangePwd;
    }

    public void setNeedChangePwd(boolean needChangePwd) {
        this.needChangePwd = needChangePwd;
    }
}
