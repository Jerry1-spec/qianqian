<template>
  <div class="page">
    <div class="header">
      <h2>我的周报</h2>
      <div>
        <el-button type="primary" @click="goCreate">新建周报</el-button>
        <el-button @click="onLogout">退出</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column prop="weekYear" label="周报周期" width="140" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitAt" label="提交时间" width="200">
        <template #default="{ row }">{{ row.submitAt || '—' }}</template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button v-if="row.status === 'draft'" link type="primary" @click="goEdit(row.id)">
            继续编辑
          </el-button>
          <el-button link type="primary" @click="goDetail(row.id)">查看</el-button>
        </template>
      </el-table-column>
      <template #empty>暂无周报，点击「新建周报」开始填写</template>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getReportList } from '../../api/report'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const list = ref([])

const statusMap = {
  draft: { text: '草稿', type: 'info' },
  submitted: { text: '待批阅', type: 'warning' },
  reviewed: { text: '已批阅', type: 'success' }
}
const statusText = (s) => statusMap[s]?.text || s
const statusTagType = (s) => statusMap[s]?.type || 'info'

async function loadList() {
  loading.value = true
  try {
    list.value = await getReportList()
  } finally {
    loading.value = false
  }
}

const goCreate = () => router.push('/student/report/edit')
const goEdit = (id) => router.push({ path: '/student/report/edit', query: { id } })
const goDetail = (id) => router.push(`/student/report/${id}`)

function onLogout() {
  userStore.logout()
  router.replace('/login')
}

onMounted(loadList)
</script>

<style scoped>
.page { padding: 24px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
