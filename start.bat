@echo off
chcp 65001 >nul
setlocal

REM ============================================================
REM  研究生周报助手 V1.0 一键启动脚本（H2 内存库演示模式）
REM  双击运行即可。会打开两个窗口：后端(8080) 与 前端(5173)。
REM  关闭对应窗口即可停止服务。数据为内存库，重启后清空。
REM ============================================================

set "PROJECT_ROOT=%~dp0"
set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
set "MVN=%PROJECT_ROOT%.tools\apache-maven-3.9.9\bin\mvn.cmd"
set "NODE_DIR=C:\Program Files\nodejs"
set "PATH=%JAVA_HOME%\bin;%NODE_DIR%;%PATH%"

echo [1/3] 检查环境...
where java >nul 2>nul || (echo [错误] 未找到 java，请确认 JDK 17 安装路径。& pause & exit /b 1)
if not exist "%MVN%" (echo [错误] 未找到 Maven：%MVN% & pause & exit /b 1)
if not exist "%NODE_DIR%\npm.cmd" (echo [错误] 未找到 Node.js：%NODE_DIR% & pause & exit /b 1)

echo [2/3] 构建并启动后端（H2 模式，端口 8080）...
start "周报助手-后端(8080)" cmd /k "cd /d "%PROJECT_ROOT%report-backend" && set JAVA_HOME=%JAVA_HOME% && "%MVN%" -DskipTests package && "%JAVA_HOME%\bin\java.exe" -jar target\report-backend-1.0.0.jar"

echo [3/3] 安装依赖并启动前端（端口 5173）...
start "周报助手-前端(5173)" cmd /k "cd /d "%PROJECT_ROOT%report-frontend" && if not exist node_modules ("%NODE_DIR%\npm.cmd" install) && "%NODE_DIR%\npm.cmd" run dev"

echo.
echo ============================================================
echo  已启动。请等待后端出现 "Started ReportApplication"、
echo  前端出现 "Local: http://localhost:5173/" 后，
echo  浏览器访问： http://localhost:5173
echo.
echo  导师账号： 13800000000    密码： Teacher@123
echo ============================================================
echo.
pause
endlocal
