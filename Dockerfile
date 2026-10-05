# =====================================================================
# 前后端一体化 Dockerfile
# 构建产物：单个 Spring Boot jar，内含前端静态资源 + H2 文件数据库
# 部署到任意支持 Docker 的平台（Koyeb / Render / Zeabur），无需单独的数据库服务。
# =====================================================================

# ---- 阶段 1：构建前端 ----
FROM node:20-alpine AS frontend
WORKDIR /frontend
COPY report-frontend/package*.json ./
RUN npm ci
COPY report-frontend/ ./
RUN npm run build

# ---- 阶段 2：构建后端（含前端产物）----
FROM maven:3.9-eclipse-temurin-17 AS backend
WORKDIR /app
COPY report-backend/pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY report-backend/src ./src
# 把前端构建产物拷贝到 Spring Boot 静态资源目录
COPY --from=frontend /frontend/dist ./src/main/resources/static
RUN mvn -q -DskipTests clean package

# ---- 阶段 3：运行 ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend /app/target/report-backend-1.0.0.jar app.jar
# 数据持久化目录（H2 文件库保存在此）
RUN mkdir -p /app/data
# 云平台通过 PORT 注入端口；默认 8080
EXPOSE 8080
# 免费实例（如 Koyeb 512MB）限制 JVM 堆内存，避免容器被 OOM 杀死
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=60.0", \
  "-XX:+UseSerialGC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
