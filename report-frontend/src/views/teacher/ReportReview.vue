<template>
  <div class="page">
    <div class="header">
      <h2>周报批阅</h2>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-descriptions v-loading="loading" :column="1" border>
      <el-descriptions-item label="周报周期">{{ detail.weekYear }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusTagType(detail.status)">{{ statusText(detail.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="本周完成工作">
        <pre class="content">{{ detail.contentWork }}</pre>
      </el-descriptions-item>
      <el-descriptions-item label="遇到的问题与难点">
        <pre class="content">{{ detail.contentProblem }}</pre>
      </el-descriptions-item>
      <el-descriptions-item label="下周工作计划">
        <pre class="content">{{ detail.contentNext }}</pre>
      </el-descriptions-item>
      <el-descriptions-item label="本周文献阅读总结">
        <pre class="content">{{ detail.contentLiterature }}</pre>
      </el-descriptions-item>
      <el-descriptions-item label="提交时间">{{ detail.submitAt || '—' }}</el-descriptions-item>
      <el-descriptions-item label="批阅时间">{{ detail.reviewAt || '—' }}</el-descriptions-item>
    </el-descriptions>

    <!-- 待批阅：可填写评语 -->
    <el-card v-if="detail.status === 'submitted'" class="review-card">
      <template #header>填写批阅评语</template>
      <el-input v-model="comment" type="textarea" :rows="5" maxlength="2000" show-word-limit
                placeholder="请输入评语" />
      <div class="actions">
        <el-button type="primary" :loading="submitting" @click="onReview">保存评语并完成批阅</el-button>
      </div>
    </el-card>

    <!-- 已批阅：只读展示评语 -->
    <el-card v-else-if="detail.status === 'reviewed'" class="review-card">
      <template #header>导师评语</template>
      <pre class="content">{{ detail.teacherComment }}</pre>
    </el-card>

    <!-- 草稿：学生尚未提交，不可批阅 -->
    <el-alert v-else-if="!loading && detail.status === 'draft'" type="info" :closable="false"
              title="该周报为草稿，学生尚未提交，暂不可批阅" style="margin-top: 16px" />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getStudentReports, reviewReport } from '../../api/teacher'

const route = useRoute()
const router = useRouter()
const reportId = Number(route.params.id)
const stuId = route.query.stuId

const statusMap = {
  draft: { text: '草稿', type: 'info' },
  submitted: { text: '待批阅', type: 'warning' },
  reviewed: { text: '已批阅', type: 'success' }
}
const statusText = (s) => statusMap[s]?.text || s
const statusTagType = (s) => statusMap[s]?.type || 'info'

const loading = ref(false)
const submitting = ref(false)
const detail = reactive({})
const comment = ref('')

async function load() {
  if (!stuId) {
    ElMessage.error('缺少学生标识，请从学生列表进入')
    return
  }
  loading.value = true
  try {
    // 导师侧无单条详情接口，取该生周报列表中定位当前条（详见前端契约设计）
    const reports = await getStudentReports(stuId)
    const target = reports.find((r) => r.id === reportId)
    if (!target) {
      ElMessage.error('未找到该周报')
      return
    }
    Object.assign(detail, target)
    comment.value = target.teacherComment || ''
  } finally {
    loading.value = false
  }
}

async function onReview() {
  if (!comment.value.trim()) {
    ElMessage.warning('请填写评语')
    return
  }
  submitting.value = true
  try {
    await reviewReport(reportId, { comment: comment.value })
    ElMessage.success('批阅完成')
    await load()
  } finally {
    submitting.value = false
  }
}

const goBack = () => router.push('/teacher/home')

onMounted(load)
</script>

<style scoped>
.page { padding: 24px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.content { white-space: pre-wrap; word-break: break-word; margin: 0; font-family: inherit; }
.review-card { margin-top: 16px; }
.actions { margin-top: 12px; text-align: right; }
</style>
