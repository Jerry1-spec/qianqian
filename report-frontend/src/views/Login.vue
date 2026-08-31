<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <h2 class="title">研究生周报助手</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="70px" @submit.prevent>
        <el-form-item label="手机号" prop="username">
          <el-input v-model="form.username" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            @keyup.enter="onLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="onLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <p class="hint">初始密码为手机号后 6 位，首次登录需修改密码</p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function onLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await login({ username: form.username, password: form.password })
    userStore.setLogin({
      token: data.token,
      role: data.role,
      userId: data.userId,
      needChangePwd: data.need_change_pwd
    })
    // 需强制改密 -> 改密页；否则按角色进首页
    if (data.need_change_pwd) {
      router.replace('/change-pwd')
    } else {
      router.replace(data.role === 'teacher' ? '/teacher/home' : '/student/home')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}
.login-card {
  width: 380px;
}
.title {
  text-align: center;
  margin: 0 0 20px;
}
.hint {
  text-align: center;
  color: #909399;
  font-size: 12px;
  margin: 8px 0 0;
}
</style>
