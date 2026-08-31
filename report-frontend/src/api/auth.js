import request from './request'

/**
 * 登录。返回 { token, role, userId, need_change_pwd }
 */
export function login(data) {
  return request.post('/login', data)
}

/**
 * 修改密码。data: { oldPassword, newPassword }
 */
export function changePwd(data) {
  return request.post('/user/change-pwd', data)
}

/**
 * 获取当前登录用户信息。
 */
export function getUserInfo() {
  return request.get('/user/info')
}
