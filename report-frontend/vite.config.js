import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  // 桌面端（Electron，file:// 协议）必须用相对路径；网页/云端用根路径
  base: process.env.VITE_PLATFORM === 'desktop' ? './' : '/',
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // 前端 /api 代理到后端，避免跨域
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
