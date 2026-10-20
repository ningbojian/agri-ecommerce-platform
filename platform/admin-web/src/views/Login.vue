<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-head">
        <div class="logo">乡村探宝</div>
        <div class="title">电商平台管理后台</div>
        <div class="subtitle">请使用管理员账号登录</div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入管理员用户名" size="large" clearable @keyup.enter="submit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" size="large" @keyup.enter="submit" />
        </el-form-item>
        <el-button type="primary" size="large" class="login-button" :loading="loading" @click="submit">
          登 录
        </el-button>
      </el-form>

      <div class="demo-tip">演示账号：admin / admin123</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function submit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  loading.value = true
  try {
    await auth.login({ username: form.username.trim(), password: form.password })
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/products')
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at 15% 20%, #356b48 0, #1f3d2b 45%, #142419 100%);
  padding: 24px;
}

.login-card {
  width: 400px;
  padding: 38px 36px 28px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.28);
}

.login-head {
  text-align: center;
  margin-bottom: 26px;
}

.logo {
  font-size: 26px;
  font-weight: 700;
  color: #2f7d4a;
  letter-spacing: 3px;
}

.title {
  margin-top: 8px;
  font-size: 17px;
  font-weight: 600;
}

.subtitle {
  margin-top: 6px;
  color: #93a199;
  font-size: 13px;
}

.login-button {
  width: 100%;
  margin-top: 6px;
  background: #2f7d4a;
  border-color: #2f7d4a;
}

.demo-tip {
  margin-top: 20px;
  text-align: center;
  color: #a0aca4;
  font-size: 12px;
}
</style>
