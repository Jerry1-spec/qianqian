# 研究生周报助手 前端（report-frontend）

Vue3 + Vite + Vue Router + Pinia + Axios + Element Plus。对应《技术设计补充说明》M1 骨架。

## 运行前置
- Node.js 18+（含 npm）

## 启动步骤
1. 安装依赖：`npm install`
2. 启动开发服务器：`npm run dev`，默认 http://localhost:5173
3. `/api` 已通过 Vite 代理到后端 http://localhost:8080（见 vite.config.js）

## 目录说明
- `src/router/`  路由 + 全局守卫（登录态 / 强制改密拦截 / 角色隔离）
- `src/store/`   Pinia：token、role、needChangePwd
- `src/api/`     Axios 封装（请求带 token、响应统一处理错误码）
- `src/views/`   页面（当前均为占位，业务在 M2–M4 实现）
  - `Login.vue` `ChangePwd.vue`
  - `student/` Home、ReportEdit、ReportDetail
  - `teacher/` Home、ReportReview

## 说明
当前为 M1 骨架：路由守卫、鉴权拦截器、页面占位齐全，可 `npm run dev` 运行。
各页面业务逻辑按里程碑 M2（登录/改密）、M3（学生周报）、M4（导师批阅）逐步接入。
