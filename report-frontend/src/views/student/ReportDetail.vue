<template>
  <div class="page">
    <div class="header">
      <h2>周报详情</h2>
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

    <el-card v-if="detail.teacherComment" class="comment-card">
      <template #header>导师评语</template>
      <pre class="content">{{ detail.teacherComment }}</pre>
    </el-card>
    <el-empty v-else-if="!loading" description="导师尚未批阅" :image-size="80" />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getReportDetail } from '../../api/report'

const route = useRoute()
const router = useRouter()
const reportId = route.params.id

const loading = ref(false)
const detail = reactive({})

const statusMap = {
  draft: { text: '草稿', type: 'info' },
  submitted: { text: '待批阅', type: 'warning' },
  reviewed: { text: '已批阅', type: 'success' }
}
const statusText = (s) => statusMap[s]?.text || s
const statusTagType = (s) => statusMap[s]?.type || 'info'

async function load() {
  loading.value = true
  try {
    Object.assign(detail, await getReportDetail(reportId))
  } finally {
    loading.value = false
  }
}

const goBack = () => router.push('/student/home')

onMounted(load)
</script>

<style scoped>
.page { padding: 24px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.content { white-space: pre-wrap; word-break: break-word; margin: 0; font-family: inherit; }
.comment-card { margin-top: 16px; }
</style>
