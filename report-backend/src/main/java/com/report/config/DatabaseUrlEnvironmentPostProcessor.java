package com.report.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 将云平台（如 Render/Heroku）注入的 DATABASE_URL 转换为 Spring JDBC 可用的配置。
 *
 * 平台给出的形如：postgres://user:pass@host:5432/dbname
 * Spring 需要：
 *   spring.datasource.url      = jdbc:postgresql://host:5432/dbname
 *   spring.datasource.username = user
 *   spring.datasource.password = pass
 *
 * 仅当存在 DATABASE_URL 且尚未显式提供 DB_URL 时才生效，不影响本地 h2/mysql。
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
        String databaseUrl = env.getProperty("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }
        // 已显式配置 DB_URL 则尊重之，不覆盖
        if (env.getProperty("DB_URL") != null) {
            return;
        }
        try {
            URI uri = new URI(databaseUrl);
            String userInfo = uri.getUserInfo(); // user:pass
            String username = null;
            String password = null;
            if (userInfo != null) {
                int idx = userInfo.indexOf(':');
                username = idx >= 0 ? userInfo.substring(0, idx) : userInfo;
                password = idx >= 0 ? userInfo.substring(idx + 1) : "";
            }
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath();

            Map<String, Object> props = new HashMap<>();
            props.put("spring.datasource.url", jdbcUrl);
            if (username != null) {
                props.put("spring.datasource.username", username);
            }
            if (password != null) {
                props.put("spring.datasource.password", password);
            }
            // 高优先级源，确保覆盖 application.yml 中的占位
            env.getPropertySources().addFirst(new MapPropertySource("renderDatabaseUrl", props));
        } catch (Exception e) {
            throw new IllegalStateException("无法解析 DATABASE_URL: " + databaseUrl, e);
        }
    }
}
