import { defineStore } from 'pinia'

/**
 * 用户会话状态：token、角色、是否需强制改密。持久化到 localStorage。
 */
export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    role: localStorage.getItem('role') || '',
    userId: localStorage.getItem('userId') || '',
    needChangePwd: localStorage.getItem('needChangePwd') === '1'
  }),
  getters: {
    isLogin: (state) => !!state.token
  },
  actions: {
    setLogin({ token, role, userId, needChangePwd }) {
      this.token = token
      this.role = role
      this.userId = String(userId)
      this.needChangePwd = !!needChangePwd
      localStorage.setItem('token', token)
      localStorage.setItem('role', role)
      localStorage.setItem('userId', String(userId))
      localStorage.setItem('needChangePwd', needChangePwd ? '1' : '0')
    },
    clearNeedChangePwd() {
      this.needChangePwd = false
      localStorage.setItem('needChangePwd', '0')
    },
    logout() {
      this.token = ''
      this.role = ''
      this.userId = ''
      this.needChangePwd = false
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('userId')
      localStorage.removeItem('needChangePwd')
    }
  }
})
