import request from './request'

/**
 * 个人周报列表（按时间倒序）。
 */
export function getReportList() {
  return request.get('/report/list')
}

/**
 * 保存周报（新建或保存草稿）。data 含 id(可空) + 周期 + 四段内容。返回 { id }
 */
export function saveReport(data) {
  return request.post('/report/save', data)
}

/**
 * 保存并提交（一次请求内完成，后端事务保证原子）。返回 { id }
 */
export function saveAndSubmitReport(data) {
  return request.post('/report/save-and-submit', data)
}

/**
 * 提交周报。draft -> submitted
 */
export function submitReport(id) {
  return request.put(`/report/submit/${id}`)
}

/**
 * 周报详情（含导师评语）。
 */
export function getReportDetail(id) {
  return request.get(`/report/${id}`)
}
