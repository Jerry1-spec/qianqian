import request from './request'

/**
 * 创建学生账号。data: { phone }，返回 { studentId }
 */
export function createStudent(data) {
  return request.post('/teacher/create-student', data)
}

/**
 * 名下学生列表（含各生最新周报周期与状态）。
 */
export function getStudents() {
  return request.get('/teacher/students')
}

/**
 * 查看指定学生的所有周报（完整详情，含评语）。
 */
export function getStudentReports(stuId) {
  return request.get(`/teacher/student/${stuId}/reports`)
}

/**
 * 批阅周报。data: { comment }。submitted -> reviewed
 */
export function reviewReport(reportId, data) {
  return request.put(`/teacher/report/${reportId}/review`, data)
}
