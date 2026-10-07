package com.report.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 单 jar 部署支持：前端打包产物内嵌于 classpath:static，
 * history 模式路由（登录页/学生端/导师端）直接访问时转发到 index.html。
 * 仅覆盖页面路径，不影响 /api/**、/h2-console 等接口。
 */
@Controller
public class SpaForwardController {

    @GetMapping({"/", "/login", "/change-pwd", "/student/**", "/teacher/**"})
    public String forwardSpa() {
        return "forward:/index.html";
    }
}
