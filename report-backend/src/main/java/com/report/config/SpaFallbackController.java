package com.report.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA 前端路由回退。
 *
 * 前后端一体化部署后，前端构建产物放在 classpath:/static/，由 Spring Boot 直接托管。
 * Vue Router 使用 history 模式，用户直接访问或刷新 /login、/teacher/home 等路径时，
 * 这些路径没有对应的静态文件，需要转发回 index.html 由前端路由处理。
 *
 * 匹配规则：路径不包含点（.）的请求（即非静态资源如 .js/.css/.png），
 * 且未被更具体的 @RequestMapping（如 /api/**）匹配时，由本控制器转发到 index.html。
 */
@Controller
public class SpaFallbackController {

    @RequestMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}
