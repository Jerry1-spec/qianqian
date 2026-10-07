# 周报助手 Capacitor 打包 Android APK 实施计划

## Context（背景与目标）

Web 版周报助手（前端 Vue3 + 后端 Spring Boot）已完成并部署上线 Render：
- 前端：https://report-frontend-un2k.onrender.com
- 后端：https://report-backend-8raw.onrender.com/api

用户希望保留全部 Web 功能，把它打包成 Android APK，侧载到华为手机使用，不上架应用市场。本机已装 Android Studio。

技术路线：**Capacitor 6** —— 把现有 Vue dist 嵌入 Android WebView。无侵入、无需重写 UI、复用现有 axios 调用 Render 后端公网地址。

## 关键约束（来自前期探查）

1. Vue Router 当前是 `createWebHistory`（history 模式），WebView 加载本地 `dist/index.html` 时刷新会 404 → 必须改 `createWebHashHistory`。
2. Vite 默认 `base: '/'`，WebView 用 `file://` 或 `https://localhost` 加载本地资源会失败 → 必须加 `base: './'`。
3. 后端 API 地址 `.env.production` 已配置 `https://report-backend-8raw.onrender.com/api`，APK 内 WebView 能直接访问。
4. 后端 CORS 在 [WebConfig.java](file:///d:/周报助手/report-backend/src/main/java/com/report/config/WebConfig.java) 通过 `APP_CORS_ALLOWED_ORIGINS` 环境变量控制。Capacitor Android 默认 origin 是 `https://localhost`，需要在 Render 后端环境变量里追加这个 origin。
5. Element Plus 在手机上能用，UI 深度适配留待后续迭代，不阻塞本次打包。

## 实施步骤

### Step 1：修改前端两处代码

**[vite.config.js](file:///d:/周报助手/report-frontend/vite.config.js)**：加 `base: './'`

```js
export default defineConfig({
  plugins: [vue()],
  base: './',          // 新增：相对路径，让 WebView 能加载本地资源
  server: { /* 保持不变 */ }
})
```

**[src/router/index.js](file:///d:/周报助手/report-frontend/src/router/index.js)**：history → hash

```js
import { createRouter, createWebHashHistory } from 'vue-router'
// ...
const router = createRouter({
  history: createWebHashHistory(),
  routes
})
```

### Step 2：安装 Capacitor 依赖

在 `report-frontend/` 目录下：

```bash
npm install @capacitor/core
npm install -D @capacitor/cli
npm install @capacitor/android
```

### Step 3：初始化 Capacitor

```bash
npx cap init "周报助手" "com.zbzs.report" --web-dir=dist
```

生成 `capacitor.config.ts`，内容确认如下：

```ts
import type { CapacitorConfig } from '@capacitor/cli'
const config: CapacitorConfig = {
  appId: 'com.zbzs.report',
  appName: '周报助手',
  webDir: 'dist',
  server: { androidScheme: 'https' }
}
export default config
```

### Step 4：构建前端 + 添加 Android 平台

```bash
npm run build                # 产出 dist/
npx cap add android          # 创建 android/ 工程（仅首次）
npx cap copy android         # 把 dist/ 同步到 android/app/src/main/assets/public/
```

后续每次改前端只需：`npm run build && npx cap copy android`。

### Step 5：在 Render 后端追加 Capacitor origin（必须）

登录 Render 控制台 → report-backend → Environment，修改 `APP_CORS_ALLOWED_ORIGINS`：

```
https://report-frontend-un2k.onrender.com,https://localhost
```

改完无需 redeploy，下次请求即生效（或手动触发 restart）。

### Step 6：用 Android Studio 生成 APK

```bash
npx cap open android     # 自动用 Android Studio 打开 android/ 工程
```

在 Android Studio 中：
- 等待 Gradle sync 完成（首次会下载依赖，约 3-5 分钟）
- 菜单 `Build → Build Bundle(s)/APK(s) → Build APK(s)`
- 完成后点通知 `locate` 找到 `app-debug.apk`

产物路径：`report-frontend/android/app/build/outputs/apk/debug/app-debug.apk`

## 验证（端到端）

1. **华为手机开启未知来源**：设置 → 安全 → 更多设置 → 允许安装外部来源应用（EMUI 路径略有差异；HarmonyOS 在"设置 > 应用 > 隐私 > 应用安装管理"）
2. **传 APK 到手机**：USB 拷贝 / 微信发送自己 / 华为分享均可
3. **安装并打开 APP**：应见登录页（URL 是 `https://localhost/#/login`）
4. **登录链路**：用导师测试账号 `13800000000 / Teacher@123` 登录 → 进入导师首页 → 能看到学生列表
5. **若登录失败且 Network 报 CORS 错**：回 Step 5 确认 origin 已加 `https://localhost`
6. **若刷新白屏**：检查 router 是否已改 hash、vite base 是否为 './'
7. **退出重进**：关闭 APP 再打开应保留登录态（token 存 localStorage，WebView 不清）

## 不做什么（避免过度工程化）

- ❌ 不做 Element Plus 移动端深度适配（先可用，UI 迭代留后续）
- ❌ 不接 `@capacitor/preferences` 等原生存储插件（localStorage 在 WebView 持久化即可）
- ❌ 不配签名 keystore、不上架应用市场（debug APK 侧载足够）
- ❌ 不接 splashscreen / statusbar / haptics 等美化插件（默认行为够用）
- ❌ 不做离线缓存 / Service Worker（依赖在线后端）
- ❌ 不改 axios baseURL 逻辑（环境变量已正确分流）
- ❌ 不混淆/加固 APK

## 关键文件清单

- [report-frontend/vite.config.js](file:///d:/周报助手/report-frontend/vite.config.js) — 改 base
- [report-frontend/src/router/index.js](file:///d:/周报助手/report-frontend/src/router/index.js) — history → hash
- [report-frontend/capacitor.config.ts](file:///d:/周报助手/report-frontend/capacitor.config.ts) — 新建
- [report-frontend/package.json](file:///d:/周报助手/report-frontend/package.json) — 加 3 个 capacitor 依赖
- Render 后端环境变量 `APP_CORS_ALLOWED_ORIGINS` — 追加 `https://localhost`
- 后端 [WebConfig.java](file:///d:/周报助手/report-backend/src/main/java/com/report/config/WebConfig.java) — **不改代码**，仅靠环境变量控制

## 回退方案

如 APK 在华为手机上登录报 CORS 而又无法立即改 Render 环境变量，临时可把后端 `WebConfig.java` 的 `allowedOriginPatterns` 默认值改成 `*` 重部署——但不推荐长期使用。
