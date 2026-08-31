@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM ============================================================
REM  一键推送到 GitHub（Jerry1-spec/qianqian）
REM  双击运行。首次会弹出 GitHub 登录窗口，按提示授权一次即可。
REM  前提：科学上网客户端(本地代理 127.0.0.1:10808)已开启。
REM ============================================================

set "GIT=C:\Program Files\Git\cmd\git.exe"
REM 去掉路径结尾的反斜杠，避免拼进引号参数时转义出错
set "REPO_DIR=%~dp0"
if "%REPO_DIR:~-1%"=="\" set "REPO_DIR=%REPO_DIR:~0,-1%"

if not exist "%GIT%" goto no_git

echo [1/3] 确认 git 走本地 SOCKS5 代理...
"%GIT%" config --global http.proxy socks5://127.0.0.1:10808
"%GIT%" config --global https.proxy socks5://127.0.0.1:10808

echo.
echo [2/3] 暂存并提交改动（若无改动会自动跳过）...
"%GIT%" -C "%REPO_DIR%" add .
"%GIT%" -C "%REPO_DIR%" diff --cached --quiet
if errorlevel 1 (
  "%GIT%" -C "%REPO_DIR%" commit -m "chore: update"
) else (
  echo     没有新的改动，直接推送已有提交。
)

echo.
echo [3/3] 推送到 GitHub（首次会弹出登录窗口，请完成授权）...
"%GIT%" -C "%REPO_DIR%" push -u origin main
set "PUSH_RESULT=%ERRORLEVEL%"

echo.
if "%PUSH_RESULT%"=="0" goto ok
goto fail

:ok
echo ============================================================
echo  推送成功！打开仓库查看：
echo  https://github.com/Jerry1-spec/qianqian
echo ============================================================
goto end

:fail
echo [推送失败] 常见原因与处理：
echo   1) 认证窗口未完成登录：重新运行本脚本，按弹窗登录 GitHub。
echo   2) 提示输入密码：GitHub 不支持密码，请用 Personal Access Token。
echo      GitHub - Settings - Developer settings - Personal access tokens
echo      生成时勾选 repo 权限；用户名填 Jerry1-spec，密码处粘贴 token。
echo   3) 连接被重置：确认代理客户端已开启，且端口为 10808。
goto end

:no_git
echo [错误] 未找到 git：%GIT%
echo 请确认 Git for Windows 已安装，或修改本脚本顶部的 GIT 路径。
goto end

:end
echo.
echo （按任意键关闭本窗口）
pause >nul
endlocal
