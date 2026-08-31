package com.report.util;

/**
 * 当前登录用户上下文（请求级 ThreadLocal），由 JWT 拦截器写入、请求结束清理。
 */
public class UserContext {

    public record CurrentUser(Long userId, String role) {
    }

    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    public static void set(CurrentUser user) {
        HOLDER.set(user);
    }

    public static CurrentUser get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        CurrentUser u = HOLDER.get();
        return u == null ? null : u.userId();
    }

    public static String getRole() {
        CurrentUser u = HOLDER.get();
        return u == null ? null : u.role();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
