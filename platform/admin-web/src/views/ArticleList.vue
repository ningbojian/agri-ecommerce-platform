<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>资讯管理</h2>
        <div class="sub">小程序「资讯」栏目展示的内容</div>
      </div>
      <el-button type="primary" @click="openCreate">新增资讯</el-button>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="标题">
          <el-input v-model="filters.keyword" clearable placeholder="资讯标题" style="width: 220px" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="全部" style="width: 120px">
            <el-option label="已发布" :value="1" />
            <el-option label="草稿" :value="0" />
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
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="封面" width="90">
          <template #default="{ row }">
            <el-image
              v-if="row.coverUrl"
              :src="row.coverUrl"
              :preview-src-list="[row.coverUrl]"
              preview-teleported
              fit="cover"
              style="width: 56px; height: 40px; border-radius: 5px"
            />
            <span v-else class="muted">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="110" />
        <el-table-column prop="summary" label="摘要" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="95">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="165">
          <template #default="{ row }">{{ formatTime(row.publishTime || row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
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

    <el-dialog v-model="dialog.visible" :title="dialog.id ? '编辑资讯' : '新增资讯'" width="720px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="84px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" maxlength="128" placeholder="请输入资讯标题" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="作者">
              <el-input v-model="form.author" maxlength="64" placeholder="作者 / 来源" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="发布" inactive-text="草稿" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面地址">
          <el-input v-model="form.coverUrl" maxlength="512" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="正文" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="12" placeholder="请输入正文内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteArticle,
  getArticlePage,
  saveArticle,
  updateArticle
} from '../api/product'
import { formatTime } from '../utils/format'

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const total = ref(0)
const formRef = ref()

const query = reactive({ current: 1, limit: 10 })
const filters = reactive({ keyword: '', status: '' })
const dialog = reactive({ visible: false, id: null })
const form = reactive(emptyForm())

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入正文', trigger: 'blur' }]
}

function emptyForm() {
  return { title: '', summary: '', content: '', coverUrl: '', author: '', status: 1 }
}

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
    const data = await getArticlePage(cleanParams())
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

function openCreate() {
  Object.assign(form, emptyForm())
  dialog.id = null
  dialog.visible = true
}

function openEdit(row) {
  Object.assign(form, {
    title: row.title,
    summary: row.summary || '',
    content: row.content || '',
    coverUrl: row.coverUrl || '',
    author: row.author || '',
    status: row.status
  })
  dialog.id = row.id
  dialog.visible = true
}

async function submit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  const payload = {
    title: form.title.trim(),
    summary: form.summary,
    content: form.content,
    coverUrl: form.coverUrl,
    author: form.author,
    status: form.status
  }
  saving.value = true
  try {
    if (dialog.id) {
      await updateArticle(dialog.id, payload)
      ElMessage.success('资讯已更新')
    } else {
      await saveArticle(payload)
      ElMessage.success('资讯已创建')
    }
    dialog.visible = false
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除资讯「${row.title}」吗？`, '删除资讯', { type: 'warning' })
  } catch (e) {
    return
  }
  try {
    await deleteArticle(row.id)
    ElMessage.success('资讯已删除')
    if (rows.value.length === 1 && query.current > 1) query.current -= 1
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  }
}

onMounted(load)
</script>
