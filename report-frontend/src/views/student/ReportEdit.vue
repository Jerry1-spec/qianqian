<template>
  <div class="page">
    <div class="header">
      <h2>{{ reportId ? '编辑周报' : '新建周报' }}</h2>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 760px">
      <el-form-item label="周报周期" prop="weekYear">
        <el-input v-model="form.weekYear" placeholder="格式：年-周，例如 2026-34" style="width: 240px" />
      </el-form-item>
      <el-form-item label="本周完成工作" prop="contentWork">
        <el-input v-model="form.contentWork" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="遇到的问题与难点" prop="contentProblem">
        <el-input v-model="form.contentProblem" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="下周工作计划" prop="contentNext">
        <el-input v-model="form.contentNext" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="本周文献阅读总结" prop="contentLiterature">
        <el-input v-model="form.contentLiterature" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item>
        <el-button :loading="saving" @click="onSaveDraft">保存草稿</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">提交周报</el-button>
      </el-form-item>
    </el-form>
    <p class="tip">提交后周报将锁定，不可再编辑，仅可查看。</p>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveReport, saveAndSubmitReport, getReportDetail } from '../../api/report'

const route = useRoute()
const router = useRouter()

// 编辑已有草稿：/student/report/edit?id=xxx（详见技术补充 1.5）
const reportId = ref(route.query.id ? Number(route.query.id) : null)

const formRef = ref()
const saving = ref(false)
const submitting = ref(false)
const form = reactive({
  weekYear: '',
  contentWork: '',
  contentProblem: '',
  contentNext: '',
  contentLiterature: ''
})

const rules = {
  weekYear: [
    { required: true, message: '请输入周报周期', trigger: 'blur' },
    { pattern: /^\d{4}-\d{1,2}$/, message: '格式应为 年-周，例如 2026-34', trigger: 'blur' }
  ],
  contentWork: [{ required: true, message: '请填写本周完成工作', trigger: 'blur' }],
  contentProblem: [{ required: true, message: '请填写遇到的问题与难点', trigger: 'blur' }],
  contentNext: [{ required: true, message: '请填写下周工作计划', trigger: 'blur' }],
  contentLiterature: [{ required: true, message: '请填写本周文献阅读总结', trigger: 'blur' }]
}

async function loadDetail() {
  if (!reportId.value) return
  const data = await getReportDetail(reportId.value)
  // 仅草稿允许编辑，非草稿跳回详情只读页
  if (data.status !== 'draft') {
    ElMessage.warning('该周报已提交，仅可查看')
    router.replace(`/student/report/${reportId.value}`)
    return
  }
  form.weekYear = data.weekYear
  form.contentWork = data.contentWork
  form.contentProblem = data.contentProblem
  form.contentNext = data.contentNext
  form.contentLiterature = data.contentLiterature
}

async function onSaveDraft() {
  await formRef.value.validate()
  saving.value = true
  try {
    const data = await saveReport({ id: reportId.value, ...form })
    reportId.value = data.id // 新建后回填 id，便于继续保存
    ElMessage.success('草稿已保存')
  } finally {
    saving.value = false
  }
}

async function onSubmit() {
  await formRef.value.validate()
  await ElMessageBox.confirm('提交后不可再编辑，确认提交？', '提示', { type: 'warning' })
  submitting.value = true
  try {
    // 保存并提交合并为单次请求，后端事务保证原子性
    await saveAndSubmitReport({ id: reportId.value, ...form })
    ElMessage.success('提交成功')
    router.replace('/student/home')
  } finally {
    submitting.value = false
  }
}

const goBack = () => router.push('/student/home')

onMounted(loadDetail)
</script>

<style scoped>
.page { padding: 24px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.tip { color: #909399; font-size: 12px; margin-top: 8px; }
</style>
