<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>供应商管理</h2>
        <div class="sub">供应商密码仅用于创建/重置，服务端加密保存且不会返回</div>
      </div>
      <el-button type="primary" @click="openCreate">新增供应商</el-button>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="名称 / 电话" style="width: 200px" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="审核">
          <el-select v-model="filters.auditStatus" clearable placeholder="全部" style="width: 120px">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="全部" style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
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
        <el-table-column label="Logo" width="68">
          <template #default="{ row }">
            <el-image
              v-if="row.logo"
              :src="row.logo"
              :preview-src-list="[row.logo]"
              preview-teleported
              fit="cover"
              style="width: 46px; height: 46px; border-radius: 6px"
            />
            <span v-else class="muted">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="supname" label="供应商名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="suptel" label="联系电话" width="118" />
        <el-table-column prop="cardnum" label="证件号" width="118" show-overflow-tooltip />
        <el-table-column prop="descript" label="简介" min-width="120" show-overflow-tooltip />
        <el-table-column label="审核" width="78">
          <template #default="{ row }">
            <el-tag :type="auditTagType(row.auditStatus)" size="small">{{ auditText(row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="启用" width="76">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="value => changeStatus(row, value)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="135">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="175" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.auditStatus === 0" link type="success" @click="audit(row, 1)">通过</el-button>
            <el-button v-if="row.auditStatus === 0" link type="warning" @click="audit(row, 2)">驳回</el-button>
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

    <el-dialog v-model="dialog.visible" :title="dialog.id ? '编辑供应商' : '新增供应商'" width="600px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="供应商名称" prop="supname">
          <el-input v-model="form.supname" maxlength="64" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="联系电话" prop="suptel">
          <el-input v-model="form.suptel" maxlength="11" placeholder="11 位手机号" />
        </el-form-item>
        <el-form-item label="证件号">
          <el-input v-model="form.cardnum" maxlength="64" placeholder="统一社会信用代码 / 身份证号" />
        </el-form-item>
        <el-form-item label="Logo 地址">
          <el-input v-model="form.logo" maxlength="512" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.descript" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="登录密码" prop="suppass">
          <el-input
            v-model="form.suppass"
            type="password"
            show-password
            maxlength="64"
            :placeholder="dialog.id ? '留空表示不修改密码' : '至少 6 位'"
          />
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
  auditSupplier,
  deleteSupplier,
  getSupplierPage,
  saveSupplier,
  setSupplierStatus,
  updateSupplier
} from '../api/sup'
import { formatTime } from '../utils/format'

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const total = ref(0)
const formRef = ref()

const query = reactive({ current: 1, limit: 10 })
const filters = reactive({ keyword: '', auditStatus: '', status: '' })
const dialog = reactive({ visible: false, id: null })
const form = reactive(emptyForm())

const rules = {
  supname: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
  suptel: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  suppass: [
    {
      validator: (rule, value, callback) => {
        if (!dialog.id && !value) return callback(new Error('新增供应商必须设置密码'))
        if (value && value.length < 6) return callback(new Error('密码至少 6 位'))
        return callback()
      },
      trigger: 'blur'
    }
  ]
}

function emptyForm() {
  return { supname: '', suptel: '', cardnum: '', logo: '', descript: '', suppass: '' }
}

function cleanParams() {
  const params = { current: query.current, limit: query.limit, ...filters }
  Object.keys(params).forEach(key => {
    if (params[key] === '' || params[key] === null || params[key] === undefined) delete params[key]
  })
  return params
}

function auditText(status) {
  return { 0: '待审核', 1: '已通过', 2: '已驳回' }[status] || '未知'
}

function auditTagType(status) {
  return { 0: 'warning', 1: 'success', 2: 'danger' }[status] || 'info'
}

async function load() {
  loading.value = true
  try {
    const data = await getSupplierPage(cleanParams())
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
  Object.assign(filters, { keyword: '', auditStatus: '', status: '' })
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
    supname: row.supname,
    suptel: row.suptel,
    cardnum: row.cardnum || '',
    logo: row.logo || '',
    descript: row.descript || '',
    suppass: ''
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
    suptel: form.suptel.trim(),
    supname: form.supname.trim(),
    cardnum: form.cardnum,
    logo: form.logo,
    descript: form.descript
  }
  if (form.suppass) payload.suppass = form.suppass
  saving.value = true
  try {
    if (dialog.id) {
      await updateSupplier({ ...payload, id: dialog.id })
      ElMessage.success('供应商已更新')
    } else {
      await saveSupplier(payload)
      ElMessage.success('供应商已创建')
    }
    dialog.visible = false
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  } finally {
    saving.value = false
  }
}

async function audit(row, status) {
  try {
    await ElMessageBox.confirm(
      `确定将供应商「${row.supname}」标记为${status === 1 ? '审核通过' : '审核驳回'}吗？`,
      '审核供应商',
      { type: 'warning' }
    )
  } catch (e) {
    return
  }
  try {
    await auditSupplier(row.id, status)
    ElMessage.success('审核状态已更新')
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  }
}

async function changeStatus(row, value) {
  try {
    await setSupplierStatus(row.id, value)
    ElMessage.success(value === 1 ? '供应商已启用' : '供应商已停用')
  } catch (e) {
    row.status = value === 1 ? 0 : 1
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除供应商「${row.supname}」吗？`, '删除供应商', { type: 'warning' })
  } catch (e) {
    return
  }
  try {
    await deleteSupplier(row.id)
    ElMessage.success('供应商已删除')
    if (rows.value.length === 1 && query.current > 1) query.current -= 1
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  }
}

onMounted(load)
</script>
