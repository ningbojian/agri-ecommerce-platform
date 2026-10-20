<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>用户管理</h2>
        <div class="sub">查看小程序注册用户（不展示密码等敏感信息）</div>
      </div>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="手机号 / 昵称" style="width: 220px" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="全部" style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="已禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="84">
          <template #default="{ row }">
            <el-avatar v-if="row.avatar" :src="row.avatar" :size="42" />
            <el-avatar v-else :size="42">{{ (row.nickname || row.tel || '?').slice(0, 1) }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="tel" label="手机号" width="150" />
        <el-table-column prop="nickname" label="昵称" min-width="140" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="最近更新" width="170">
          <template #default="{ row }">{{ formatTime(row.updateTime) }}</template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="query.current"
          v-model:page-size="query.limit"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="load"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getUserPage } from '../api/user'
import { formatTime } from '../utils/format'

const loading = ref(false)
const rows = ref([])
const total = ref(0)

const query = reactive({ current: 1, limit: 10 })
const filters = reactive({ keyword: '', status: '' })

function cleanParams() {
  const params = { current: query.current, limit: query.limit, ...filters }
  Object.keys(params).forEach(key => {
    if (params[key] === '' || params[key] === null || params[key] === undefined) delete params[key]
  })
  return params
}

async function load() {
  loading.value = true
  try {
    const data = await getUserPage(cleanParams())
    rows.value = data.records || []
    total.value = Number(data.total || 0)
  } catch (e) {
    // 错误提示由拦截器处理
  } finally {
    loading.value = false
  }
}

function search() {
  query.current = 1
  load()
}

function resetFilters() {
  Object.assign(filters, { keyword: '', status: '' })
  search()
}

function handleSizeChange() {
  query.current = 1
  load()
}

onMounted(load)
</script>
