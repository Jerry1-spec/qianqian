# 部署到 Render（免费上线）操作手册

本手册带你把「研究生周报助手」免费部署到公网。分工：
- **代码/配置**：已全部就绪（PostgreSQL 适配、Dockerfile、render.yaml、环境变量化）。
- **需要你操作**：注册账号、把代码推到 GitHub、在 Render 点几下（这些需要你的账号，无法代劳）。

> ⚠️ 免费层须知：后端 **15 分钟无访问会休眠**，下次访问冷启动 30~60 秒；免费数据库 **约 90 天过期**需重建。适合演示/试用，不适合正式长期系统。

---

## 一、准备 GitHub 仓库

1. 注册/登录 [github.com](https://github.com)，新建一个仓库（可设为 Private）。
2. 在项目根目录 `D:\周报助手` 执行（首次）：

```bash
git init
git add .
git commit -m "feat: 研究生周报助手 V1.0，支持 Render 免费部署"
git branch -M main
git remote add origin https://github.com/你的用户名/仓库名.git
git push -u origin main
```

> `.gitignore` 已配置，不会提交 `target/`、`node_modules/`、`.tools/`、日志与本地密钥。

---

## 二、用 Blueprint 一键创建（推荐）

1. 登录 [render.com](https://render.com)（可用 GitHub 账号登录），授权访问你的仓库。
2. 顶部 **New +** → **Blueprint** → 选中刚推送的仓库。
3. Render 会自动读取根目录的 `render.yaml`，创建三样东西：
   - `report-db`：免费 PostgreSQL
   - `report-backend`：后端（Docker 构建）
   - `report-frontend`：前端静态站
4. 点 **Apply**，等待构建（首次约 5~10 分钟）。

---

## 三、部署后需手动补两个环境变量（互相指向）

因为前后端地址在创建后才生成，需回填一次：

1. 后端 `report-backend` → Environment → 找到 `APP_CORS_ALLOWED_ORIGINS`
   填前端地址，例如：`https://report-frontend-xxxx.onrender.com`
2. 前端 `report-frontend` → Environment → 找到 `VITE_API_BASE`
   填后端地址 + `/api`，例如：`https://report-backend-xxxx.onrender.com/api`
3. 两者都改完后各自 **Manual Deploy → Deploy latest commit** 重新构建生效。

---

## 四、首次建表说明

- `render.yaml` 里后端 `DB_INIT_MODE=always`，**首次启动会自动建表并创建导师账号**。
- 建成功后，建议把它改为 `never`（后端 Environment 里改），避免每次重启都尝试初始化。
  （脚本用 `IF NOT EXISTS` 和 `ON CONFLICT DO NOTHING`，即使保持 always 也不会破坏已有数据。）

---

## 五、访问与账号

- 打开前端地址：`https://report-frontend-xxxx.onrender.com`
- 导师账号：`13800000000` / 密码 `Teacher@123`
- 学生由导师创建，初始密码 = 手机号后 6 位，首次登录强制改密。

---

## 六、常见问题

| 现象 | 原因 / 解决 |
|------|------------|
| 第一次打开很慢、转圈 | 后端免费层休眠，冷启动 30~60 秒，稍等重试 |
| 登录报网络异常 / CORS 错误 | `APP_CORS_ALLOWED_ORIGINS` 未填或填错前端地址；`VITE_API_BASE` 未填后端地址 |
| 登录报服务器错误 | 数据库未初始化：确认后端 `DB_INIT_MODE=always` 且已重新部署 |
| 数据突然清空 | 免费库到期（约 90 天）或被回收；需重建数据库并重新初始化 |
| 想长期稳定 | 升级 Render 付费层，或改用学生云服务器（见《运行说明.md》） |

---

## 七、本地三种运行模式对照

| 模式 | 激活方式 | 数据库 | 用途 |
|------|---------|--------|------|
| H2 | 默认 / `SPRING_PROFILES_ACTIVE=h2` | 内存，重启清空 | 本地零依赖演示（`start.bat`） |
| MySQL | `SPRING_PROFILES_ACTIVE=mysql` | 本地 MySQL | 本地持久化 |
| PostgreSQL | `SPRING_PROFILES_ACTIVE=postgres` | 云 PG | Render 等云端部署 |
