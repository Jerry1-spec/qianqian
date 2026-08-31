<template>
  <div class="page">
    <div class="header">
      <h2>我的学生</h2>
      <div>
        <el-button type="primary" @click="openCreate">创建学生账号</el-button>
        <el-button @click="onLogout">退出</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="students" border stripe>
      <el-table-column prop="username" label="学生账号（手机号）" width="200" />
      <el-table-column label="最新周报周期" width="160">
        <template #default="{ row }">{{ row.latestWeekYear || '—' }}</template>
      </el-table-column>
      <el-table-column label="最新周报状态" width="160">
        <template #default="{ row }">
          <el-tag v-if="row.latestStatus" :type="statusTagType(row.latestStatus)">
            {{ statusText(row.latestStatus) }}
          </el-tag>
          <span v-else>暂无周报</span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewReports(row)">查看周报</el-button>
        </template>
      </el-table-column>
      <template #empty>暂无学生，点击「创建学生账号」添加</template>
    </el-table>

    <!-- 某学生的周报列表抽屉 -->
    <el-drawer v-model="drawerVisible" :title="`${currentStudent?.username || ''} 的周报`" size="52%">
      <el-table v-loading="reportsLoading" :data="studentReports" border>
        <el-table-column prop="weekYear" label="周期" width="120" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitAt" label="提交时间">
          <template #default="{ row }">{{ row.submitAt || '—' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="goReview(row)">
              {{ row.status === 'submitted' ? '批阅' : '查看' }}
            </el-button>
          </template>
        </el-table-column>
        <template #empty>该学生暂无周报</template>
      </el-table>
    </el-drawer>

    <!-- 创建学生账号弹窗 -->
    <el-dialog v-model="dialogVisible" title="创建学生账号" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" @submit.prevent>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入学生手机号" maxlength="11" />
        </el-form-item>
      </el-form>
      <p class="tip">账号为手机号，初始密码为手机号后 6 位，学生首次登录需修改密码。</p>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="onSubmit">确认创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createStudent, getStudents, getStudentReports } from '../../api/teacher'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()

const statusMap = {
  draft: { text: '草稿', type: 'info' },
  submitted: { text: '待批阅', type: 'warning' },
  reviewed: { text: '已批阅', type: 'success' }
}
const statusText = (s) => statusMap[s]?.text || s
const statusTagType = (s) => statusMap[s]?.type || 'info'

// 学生列表
const loading = ref(false)
const students = ref([])
async function loadStudents() {
  loading.value = true
  try {
    students.value = await getStudents()
  } finally {
    loading.value = false
  }
}

// 某学生周报抽屉
const drawerVisible = ref(false)
const reportsLoading = ref(false)
const currentStudent = ref(null)
const studentReports = ref([])
async function viewReports(row) {
  currentStudent.value = row
  drawerVisible.value = true
  reportsLoading.value = true
  try {
    studentReports.value = await getStudentReports(row.studentId)
  } finally {
    reportsLoading.value = false
  }
}
function goReview(report) {
  // 批阅页通过 query 带 stuId，进页后据此拉取该生周报定位当前条
  router.push({ path: `/teacher/report/${report.id}`, query: { stuId: currentStudent.value.studentId } })
}

// 创建学生
const dialogVisible = ref(false)
const creating = ref(false)
const formRef = ref()
const form = reactive({ phone: '' })
const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}
function openCreate() {
  form.phone = ''
  dialogVisible.value = true
}
async function onSubmit() {
  await formRef.value.validate()
  creating.value = true
  try {
    await createStudent({ phone: form.phone })
    ElMessage.success(`创建成功！初始密码为手机号后6位：${form.phone.slice(-6)}`)
    dialogVisible.value = false
    loadStudents()
  } finally {
    creating.value = false
  }
}

function onLogout() {
  userStore.logout()
  router.replace('/login')
}

onMounted(loadStudents)
</script>

<style scoped>
.page { padding: 24px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.tip { color: #909399; font-size: 12px; margin: 4px 0 0; }
</style>
