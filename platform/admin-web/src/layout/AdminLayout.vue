<template>
  <el-container class="admin-layout">
    <el-aside width="224px" class="sidebar">
      <div class="brand">
        <div class="brand-title">乡村探宝</div>
        <div class="brand-sub">电商平台管理后台</div>
      </div>
      <el-menu :default-active="activeMenu" router class="sidebar-menu" background-color="#1f3d2b" text-color="#c9d8cc" active-text-color="#ffffff">
        <el-menu-item index="/products">商品管理</el-menu-item>
        <el-menu-item index="/categories">分类管理</el-menu-item>
        <el-menu-item index="/articles">资讯管理</el-menu-item>
        <el-menu-item index="/orders">订单管理</el-menu-item>
        <el-menu-item index="/users">用户管理</el-menu-item>
        <el-menu-item index="/suppliers">供应商管理</el-menu-item>
        <el-menu-item index="/feedback">反馈管理</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="topbar">
        <div class="topbar-title">{{ pageTitle }}</div>
        <div class="topbar-right">
          <span class="admin-name">{{ auth.displayName }}</span>
          <el-button link type="danger" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '管理后台')

async function logout() {
  try {
    await ElMessageBox.confirm('确定退出当前管理员账号吗？', '退出登录', {
      type: 'warning',
      confirmButtonText: '退出',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100%;
}

.sidebar {
  display: flex;
  flex-direction: column;
  background: #1f3d2b;
}

.brand {
  padding: 22px 20px 18px;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 2px;
}

.brand-sub {
  margin-top: 6px;
  font-size: 12px;
  color: #93ac99;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  margin: 2px 10px;
  border-radius: 8px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: #2f7d4a;
  font-weight: 600;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6ece7;
  height: 60px;
}

.topbar-title {
  font-size: 16px;
  font-weight: 600;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.admin-name {
  color: #52645a;
  font-size: 14px;
}

.main {
  padding: 20px;
  background: #f3f6f3;
  overflow: auto;
}
</style>
