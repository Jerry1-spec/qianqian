package com.report.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 将云平台注入的数据库连接信息转换为 Spring JDBC 可用的配置。
 *
 * 支持两类平台变量（优先级从高到低）：
 * 1. DATABASE_URL（Render / Heroku / Zeabur 均会注入），形如：
 *      postgres://user:pass@host:5432/dbname          (Render)
 *      postgresql://user:pass@host:5432/dbname?...    (Zeabur)
 * 2. Zeabur PostgreSQL 服务注入的分散变量：
 *      POSTGRES_HOST / POSTGRES_PORT / POSTGRES_USERNAME /
 *      POSTGRES_PASSWORD / POSTGRES_DATABASE
 *
 * 转换结果：
 *   spring.datasource.url      = jdbc:postgresql://host:5432/dbname[?params]
 *   spring.datasource.username = user
 *   spring.datasource.password = pass
 *
 * 说明：
 * - 用户名/密码做 URL 解码，平台生成的随机密码可能含 @ : / 等被百分号编码的字符。
 * - 保留连接串上的查询参数（如 sslmode=require），pgjdbc 可直接识别。
 * - 仅在未显式提供 DB_URL 时生效，不影响本地 h2/mysql 配置。
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
        // 已显式配置 DB_URL 则尊重之，不覆盖
        if (env.getProperty("DB_URL") != null) {
            return;
        }

        Map<String, Object> props = new HashMap<>();

        String databaseUrl = env.getProperty("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            parseDatabaseUrl(databaseUrl, props);
        } else {
            assembleFromPostgresVars(env, props);
        }

        if (!props.isEmpty()) {
            // 高优先级源，确保覆盖 application.yml 中的占位
            env.getPropertySources().addFirst(new MapPropertySource("cloudDatabaseUrl", props));
        }
    }

    /**
     * 解析 postgres:// / postgresql:// 形式的连接串。
     */
    private void parseDatabaseUrl(String databaseUrl, Map<String, Object> props) {
        try {
            URI uri = new URI(databaseUrl);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equals("postgres") || scheme.equals("postgresql"))) {
                throw new IllegalArgumentException("仅支持 postgres:// 或 postgresql:// 连接串");
            }

            if (uri.getUserInfo() != null) {
                String userInfo = uri.getUserInfo(); // user:pass
                int idx = userInfo.indexOf(':');
                String username = idx >= 0 ? userInfo.substring(0, idx) : userInfo;
                String password = idx >= 0 ? userInfo.substring(idx + 1) : "";
                props.put("spring.datasource.username", decode(username));
                props.put("spring.datasource.password", decode(password));
            }

            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                    .append(uri.getHost()).append(':').append(port)
                    .append(uri.getPath() == null || uri.getPath().isBlank() ? "/" : uri.getPath());
            // 保留 sslmode 等查询参数，pgjdbc 原生支持
            if (uri.getRawQuery() != null && !uri.getRawQuery().isBlank()) {
                jdbcUrl.append('?').append(uri.getRawQuery());
            }
            props.put("spring.datasource.url", jdbcUrl.toString());
        } catch (Exception e) {
            throw new IllegalStateException("无法解析 DATABASE_URL: " + databaseUrl, e);
        }
    }

    /**
     * 无 DATABASE_URL 时，使用 Zeabur 注入的 POSTGRES_* 分散变量组装连接。
     */
    private void assembleFromPostgresVars(ConfigurableEnvironment env, Map<String, Object> props) {
        String host = env.getProperty("POSTGRES_HOST");
        if (host == null || host.isBlank()) {
            return;
        }
        String port = env.getProperty("POSTGRES_PORT", "5432");
        // Zeabur 官方模板使用 POSTGRES_USERNAME；兼容部分平台的 POSTGRES_USER
        String username = firstNonBlank(env.getProperty("POSTGRES_USERNAME"), env.getProperty("POSTGRES_USER"));
        String password = env.getProperty("POSTGRES_PASSWORD");
        // Zeabur 官方模板使用 POSTGRES_DATABASE；兼容 POSTGRES_DB
        String database = firstNonBlank(env.getProperty("POSTGRES_DATABASE"), env.getProperty("POSTGRES_DB"));

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + (database == null ? "" : database);
        props.put("spring.datasource.url", jdbcUrl);
        if (username != null) {
            props.put("spring.datasource.username", username);
        }
        if (password != null) {
            props.put("spring.datasource.password", password);
        }
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        return (b != null && !b.isBlank()) ? b : null;
    }
}
