package com.report.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注即使处于初始密码状态（is_init_password=1）也允许访问的接口，
 * 如：修改密码、获取当前用户信息。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowInitPassword {
}
