import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('../views/Login.vue'), meta: { public: true } },
  { path: '/change-pwd', component: () => import('../views/ChangePwd.vue'), meta: { allowInitPwd: true } },
  { path: '/student/home', component: () => import('../views/student/Home.vue'), meta: { role: 'student' } },
  { path: '/student/report/edit', component: () => import('../views/student/ReportEdit.vue'), meta: { role: 'student' } },
  { path: '/student/report/:id', component: () => import('../views/student/ReportDetail.vue'), meta: { role: 'student' } },
  { path: '/teacher/home', component: () => import('../views/teacher/Home.vue'), meta: { role: 'teacher' } },
  { path: '/teacher/report/:id', component: () => import('../views/teacher/ReportReview.vue'), meta: { role: 'teacher' } },
  { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 全局守卫：登录态 -> 强制改密拦截 -> 角色隔离。
 * 详见《技术设计补充说明》第5节。
 */
router.beforeEach((to) => {
  const userStore = useUserStore()

  // 公共页面（登录页）直接放行
  if (to.meta.public) {
    return true
  }

  // 未登录 -> 登录页
  if (!userStore.isLogin) {
    return '/login'
  }

  // 强制改密：未改密的学生只能访问 change-pwd
  if (userStore.needChangePwd && !to.meta.allowInitPwd) {
    return '/change-pwd'
  }

  // 角色隔离
  if (to.meta.role && to.meta.role !== userStore.role) {
    return userStore.role === 'teacher' ? '/teacher/home' : '/student/home'
  }

  return true
})

export default router
