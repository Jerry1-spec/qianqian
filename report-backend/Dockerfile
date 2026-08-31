# ---- 构建阶段：用 Maven + JDK17 打包 ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# 先拷贝 pom 单独下载依赖，利用 Docker 层缓存加速后续构建
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests clean package

# ---- 运行阶段：仅 JRE，体积小 ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/report-backend-1.0.0.jar app.jar
# 云平台通过 PORT 注入端口；SPRING_PROFILES_ACTIVE 选择 postgres
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
