# =====================================================================
# 前后端一体化 Dockerfile（2 阶段，适配 SnapDeploy 免费层 512MB）
# 构建产物：单个 Spring Boot jar，内含前端静态资源 + H2 文件数据库
# =====================================================================

# ---- 阶段 1：构建（前端 + 后端合一）----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 安装 Node.js + npm（Ubuntu 默认源，含 npm；Node 18 足够构建 Vite 项目）
RUN apt-get update && apt-get install -y nodejs npm \
    && rm -rf /var/lib/apt/lists/*

# 构建前端（限制 Node 内存，避免 512MB 容器 OOM）
WORKDIR /app/frontend
COPY report-frontend/package*.json ./
RUN npm ci
COPY report-frontend/ ./
RUN NODE_OPTIONS="--max-old-space-size=384" npm run build

# 构建后端（把前端产物放入 static 目录）
WORKDIR /app/backend
COPY report-backend/pom.xml .
RUN MAVEN_OPTS="-Xmx384m" mvn -q -e -DskipTests dependency:go-offline
COPY report-backend/src ./src
RUN cp -r /app/frontend/dist ./src/main/resources/static
RUN MAVEN_OPTS="-Xmx384m" mvn -q -DskipTests clean package

# ---- 阶段 2：运行 ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/backend/target/report-backend-1.0.0.jar app.jar
RUN mkdir -p /app/data
EXPOSE 8080
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=60.0", \
  "-XX:+UseSerialGC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
