import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { warmupBackend } from './api/request'

// 构建标记：确认本次构建注入的后端地址（便于线上核验，无业务影响）
console.log('[build] API_BASE =', import.meta.env.VITE_API_BASE || '/api', '| build 2026-09-01')

// App 一启动立即唤醒云容器，把冷启动（约 1 分钟）提前到用户输入账号密码期间完成，
// 这样点登录时通常服务器已就绪，实现最快登录。
warmupBackend()

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.mount('#app')
