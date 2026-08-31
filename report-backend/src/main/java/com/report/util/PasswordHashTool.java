package com.report.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 一次性工具：生成 BCrypt 密文，用于填充 sql/init.sql 中的初始导师密码。
 * 用法：直接运行本类 main 方法，传入明文密码作为参数（缺省用 Teacher@123）。
 */
public class PasswordHashTool {

    public static void main(String[] args) {
        String raw = args.length > 0 ? args[0] : "Teacher@123";
        String hash = new BCryptPasswordEncoder().encode(raw);
        System.out.println("明文: " + raw);
        System.out.println("BCrypt密文: " + hash);
        System.out.println("请将上面的密文替换到 sql/init.sql 的导师账号 password 字段。");
    }
}
