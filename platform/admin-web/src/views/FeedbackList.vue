<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>反馈管理</h2>
        <div class="sub">查看小程序用户反馈并进行回复</div>
      </div>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="全部" style="width: 140px">
            <el-option label="待处理" :value="0" />
            <el-option label="已回复" :value="1" />
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
        <el-table-column prop="id" label="ID" width="56" />
        <el-table-column prop="userid" label="用户 ID" width="78" />
        <el-table-column label="反馈内容" min-width="200">
          <template #default="{ row }">
            <div class="text-block">{{ row.content }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="contact" label="联系方式" width="120" show-overflow-tooltip />
        <el-table-column label="状态" width="84">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="回复" min-width="170">
          <template #default="{ row }">
            <div class="text-block">{{ row.reply || '—' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="138">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="回复时间" width="138">
          <template #default="{ row }">{{ formatTime(row.replyTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="82" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openReply(row)">{{ row.status === 1 ? '追评' : '回复' }}</el-button>
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

    <el-dialog v-model="dialog.visible" title="回复反馈" width="560px" :close-on-click-modal="false">
      <div class="feedback-quote">
        <div class="label">用户反馈</div>
        <div class="content text-block">{{ dialog.content }}</div>
        <div v-if="dialog.contact" class="contact">联系方式：{{ dialog.contact }}</div>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="回复内容" prop="reply">
          <el-input v-model="form.reply" type="textarea" :rows="5" maxlength="500" show-word-limit placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedbackPage, replyFeedback } from '../api/user'
import { formatTime } from '../utils/format'

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const total = ref(0)
const formRef = ref()

const query = reactive({ current: 1, limit: 10 })
const filters = reactive({ status: '' })
const dialog = reactive({ visible: false, id: null, content: '', contact: '' })
const form = reactive({ reply: '' })

const rules = {
  reply: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
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
    const data = await getFeedbackPage(cleanParams())
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
  filters.status = ''
  search()
}

function handleSizeChange() {
  query.current = 1
  load()
}

function openReply(row) {
  dialog.id = row.id
  dialog.content = row.content
  dialog.contact = row.contact
  form.reply = row.reply || ''
  dialog.visible = true
}

async function submit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  saving.value = true
  try {
    await replyFeedback(dialog.id, { reply: form.reply.trim() })
    ElMessage.success('回复已提交')
    dialog.visible = false
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.feedback-quote {
  margin-bottom: 18px;
  padding: 14px 16px;
  border-radius: 8px;
  background: #f5f8f5;
  border: 1px solid #e6ece7;
}

.feedback-quote .label {
  color: #8a978e;
  font-size: 12px;
  margin-bottom: 6px;
}

.feedback-quote .contact {
  margin-top: 8px;
  color: #8a978e;
  font-size: 12px;
}
</style>
