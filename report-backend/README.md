# 研究生周报助手 后端（report-backend）

Spring Boot 3.2 + MyBatis-Plus + MySQL 8.0 + JWT。对应《技术设计补充说明》M1 骨架。

## 运行前置
- JDK 17
- Maven 3.8+
- MySQL 8.0

## 启动步骤
1. 创建数据库并建表：在 MySQL 执行 `sql/init.sql`。
2. 生成导师密码密文：运行 `com.report.util.PasswordHashTool`（可传明文参数），
   将输出的 BCrypt 密文替换到 `sql/init.sql` 导师账号的 `password` 字段后重新插入。
3. 修改 `src/main/resources/application.yml` 中数据库账号密码、以及 `jwt.secret`。
4. 启动：`mvn spring-boot:run`，默认端口 8080。
5. 健康检查：GET http://localhost:8080/api/health 返回 `{"code":0,"msg":"success","data":"ok"}`。

## 目录说明
- `config/`  JWT 拦截器、权限注解（@RequireRole/@AllowInitPassword）、跨域
- `common/`  统一响应 Result、错误码 ErrorCode、业务异常、全局异常处理
- `controller/` 接口层（当前仅 HealthController，业务接口在 M2+ 补充）
- `entity/` `mapper/` 实体与 MyBatis-Plus Mapper
- `util/`   JwtUtil、UserContext、PasswordHashTool

## 说明
当前为 M1 骨架：可启动、鉴权基础设施就绪。登录/周报/批阅等业务接口在后续里程碑实现。
