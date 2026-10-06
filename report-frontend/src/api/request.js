import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import router from '../router'

const API_BASE = import.meta.env.VITE_API_BASE || '/api'

const request = axios.create({
  // 本地开发走 Vite 代理 /api；桌面/手机端指向后端公网地址
  baseURL: API_BASE,
  // 免费云平台冷启动期间请求可能挂起，放宽到 60 秒
  timeout: 60000
})

// 请求拦截器：附带 token
request.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

// ====================================================================
// 免费云平台（SnapDeploy）休眠主动唤醒
// 容器 15 分钟无访问会休眠，冷启动 60~90 秒。休眠时平台对所有请求返回
// 503 唤醒页 HTML。平台规定：访问容器本身不会唤醒，必须调用其唤醒接口：
//   POST https://snapdeploy.dev/api/public/wake/<子域名>
// 该接口用 CORS 安全的 text/plain 简单请求发送（不触发预检），桌面 file://
// 与手机 WebView 均可发出；即使读不到跨域响应，服务端也会开始启动容器。
// 发出唤醒信号后每 4 秒轮询原接口，容器就绪后自动继续登录/操作。
// ====================================================================
const SLEEP_RETRY_MAX = 60        // 60 次 × 2 秒 ≈ 120 秒
const SLEEP_RETRY_DELAY = 2000
const SLEEP_FIRST_DELAY = 2000    // 唤醒信号发出后首次快速探测
let sleepTipShown = false

const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

/** 从 API 地址推导平台唤醒地址；非绝对地址（同源 /api）返回 null */
function buildWakeUrl() {
  try {
    const m = String(API_BASE).match(/^https?:\/\/([^/]+)/i)
    if (!m) return null
    const host = m[1]
    const parts = host.split('.')
    if (parts.length < 3) return null
    const subdomain = parts[0]
    const hostname = host
    const platformApi =
      hostname.includes('containers.somdip.dev') ||
      hostname.includes('containers-dev.snapdeploy.app')
        ? 'https://containers.somdip.dev'
        : 'https://snapdeploy.dev'
    return `${platformApi}/api/public/wake/${subdomain}`
  } catch (e) {
    return null
  }
}
const WAKE_URL = buildWakeUrl()

/** 发出唤醒信号（简单请求，无预检；忽略一切结果，只需服务端收到） */
async function sendWakeSignal() {
  if (!WAKE_URL) return
  try {
    await fetch(WAKE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'text/plain;charset=UTF-8' },
      body: '',
    })
  } catch (e) {
    // 跨域响应可能不可读而 reject，但 POST 已发出、唤醒已触发，忽略
  }
}

/** 判断响应是否为云平台休眠唤醒页（HTML 文本 / 502-504） */
function isSleepPage(data, status) {
  const text = typeof data === 'string' ? data : ''
  return /waking up|container is sleeping|starting up|snapdeploy/i.test(text) ||
    (typeof status === 'number' && status >= 502 && status <= 504)
}

/**
 * 休眠处理：先发唤醒信号，再延迟重试原请求。
 * @returns 重试后的响应数据；重试耗尽返回 undefined
 */
async function handleSleep(config) {
  config.__sleepRetry = (config.__sleepRetry || 0) + 1
  if (config.__sleepRetry > SLEEP_RETRY_MAX) return undefined

  if (!sleepTipShown) {
    sleepTipShown = true
    ElMessage.info('服务器正在唤醒，约需 1 分钟，请勿关闭…')
  }

  // 第 1 次与之后每隔几轮补发一次唤醒信号，确保信号送达
  if (config.__sleepRetry === 1 || config.__sleepRetry % 5 === 0) {
    sendWakeSignal()
  }

  // 首次快速探测（容器可能已在预热中，尽快命中），之后按固定间隔轮询
  await delay(config.__sleepRetry === 1 ? SLEEP_FIRST_DELAY : SLEEP_RETRY_DELAY)
  return request(config)
}

/**
 * 预热：App 一启动就主动唤醒容器，把冷启动提前到用户输入账号密码期间完成。
 * 已在运行时调用无害（接口返回 already running）。
 */
export function warmupBackend() {
  sendWakeSignal()
}

// 响应拦截器：统一处理业务错误码
request.interceptors.response.use(
  async (response) => {
    const res = response.data

    // 收到休眠唤醒页 HTML：主动唤醒并重试
    if (isSleepPage(res, response.status)) {
      const retried = await handleSleep(response.config)
      if (retried !== undefined) return retried
      sleepTipShown = false
      ElMessage.error('服务器唤醒超时，请稍后再试')
      return Promise.reject(new Error('sleep timeout'))
    }

    if (res.code === 0) {
      sleepTipShown = false
      return res.data
    }
    // 40101 未登录/失效：清理并跳登录
    if (res.code === 40101) {
      const userStore = useUserStore()
      userStore.logout()
      router.replace('/login')
    }
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(res)
  },
  async (error) => {
    const config = error.config || {}
    const status = error.response && error.response.status
    const data = error.response && error.response.data

    // 冷启动期间：唤醒页 503 HTML、超时或网络错误 —— 主动唤醒并重试
    if (isSleepPage(data, status) || error.code === 'ECONNABORTED' || error.message === 'Network Error') {
      const retried = await handleSleep(config)
      if (retried !== undefined) return retried
      sleepTipShown = false
      ElMessage.error('服务器暂时不可用，请稍后重试')
      return Promise.reject(error)
    }

    ElMessage.error('网络异常，请稍后重试')
    return Promise.reject(error)
  }
)

export default request
