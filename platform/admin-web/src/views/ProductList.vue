<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>商品管理</h2>
        <div class="sub">维护商品资料、库存、上下架与审核状态</div>
      </div>
      <el-button type="primary" @click="openCreate">新增商品</el-button>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="商品名称 / 供应商" style="width: 200px" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filters.categoryId" clearable placeholder="全部分类" style="width: 150px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="上架">
          <el-select v-model="filters.publishStatus" clearable placeholder="全部" style="width: 110px">
            <el-option label="已上架" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核">
          <el-select v-model="filters.auditStatus" clearable placeholder="全部" style="width: 110px">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
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
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column label="图片" width="60">
          <template #default="{ row }">
            <el-image
              v-if="row.picurl"
              :src="row.picurl"
              :preview-src-list="[row.picurl]"
              preview-teleported
              fit="cover"
              style="width: 48px; height: 48px; border-radius: 6px"
            />
            <span v-else class="muted">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="proname" label="商品名称" min-width="135" show-overflow-tooltip />
        <el-table-column label="分类" width="82">
          <template #default="{ row }">{{ categoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column prop="supname" label="供应商" width="85" show-overflow-tooltip />
        <el-table-column label="单价" width="96">
          <template #default="{ row }">
            <span class="money">{{ formatMoney(row.price) }}</span>
            <span class="muted">/{{ row.unit || '件' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="库存/销量" width="100">
          <template #default="{ row }">{{ row.stock }} / {{ row.sales }}</template>
        </el-table-column>
        <el-table-column label="上架" width="84">
          <template #default="{ row }">
            <el-tag :type="row.publishStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.publishStatus === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核" width="84">
          <template #default="{ row }">
            <el-tag :type="auditTagType(row.auditStatus)" size="small">
              {{ auditText(row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="155">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="togglePublish(row)">
              {{ row.publishStatus === 1 ? '下架' : '上架' }}
            </el-button>
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

    <el-dialog v-model="dialog.visible" :title="dialog.id ? '编辑商品' : '新增商品'" width="680px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="proname">
              <el-input v-model="form.proname" maxlength="128" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supid">
              <el-select
                v-if="suppliers.length"
                v-model="form.supid"
                placeholder="请选择供应商"
                style="width: 100%"
                @change="onSupplierChange"
              >
                <el-option v-for="s in suppliers" :key="s.id" :label="s.supname" :value="s.id" />
              </el-select>
              <el-input-number v-else v-model="form.supid" :min="1" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商名称">
              <el-input v-model="form.supname" maxlength="64" placeholder="选择供应商后自动填充" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" :precision="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位">
              <el-input v-model="form.unit" maxlength="16" placeholder="件 / 斤 / 盒" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发货地">
              <el-input v-model="form.delivery" maxlength="64" placeholder="如：包邮" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="图片地址">
              <el-input v-model="form.picurl" maxlength="512" placeholder="https://..." />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品描述">
              <el-input v-model="form.descript" type="textarea" :rows="3" maxlength="1000" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上架状态">
              <el-switch v-model="form.publishStatus" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审核状态">
              <el-select v-model="form.auditStatus" style="width: 100%">
                <el-option label="待审核" :value="0" />
                <el-option label="审核通过" :value="1" />
                <el-option label="审核驳回" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
  auditProduct,
  deleteProduct,
  getCategories,
  getProductPage,
  publishProduct,
  saveProduct,
  updateProduct
} from '../api/product'
import { getSupplierPage } from '../api/sup'
import { formatMoney, formatTime } from '../utils/format'

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const total = ref(0)
const categories = ref([])
const suppliers = ref([])
const formRef = ref()

const query = reactive({ current: 1, limit: 10 })
const filters = reactive({ keyword: '', categoryId: '', publishStatus: '', auditStatus: '' })
const dialog = reactive({ visible: false, id: null })
const form = reactive(emptyForm())

const rules = {
  proname: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  supid: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

function emptyForm() {
  return {
    id: null,
    proname: '',
    supid: null,
    supname: '',
    categoryId: null,
    delivery: '',
    price: 0,
    stock: 0,
    unit: '件',
    descript: '',
    picurl: '',
    publishStatus: 1,
    auditStatus: 1
  }
}

function cleanParams(extra = {}) {
  const params = { current: query.current, limit: query.limit, ...filters, ...extra }
  Object.keys(params).forEach(key => {
    if (params[key] === '' || params[key] === null || params[key] === undefined) delete params[key]
  })
  return params
}

async function load() {
  loading.value = true
  try {
    const data = await getProductPage(cleanParams())
    rows.value = data.records || []
    total.value = Number(data.total || 0)
  } catch (e) {
    // 错误提示由拦截器处理
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    categories.value = (await getCategories()) || []
  } catch (e) {
    categories.value = []
  }
}

async function loadSuppliers() {
  try {
    const data = await getSupplierPage({ current: 1, limit: 100 })
    suppliers.value = data.records || []
  } catch (e) {
    suppliers.value = []
  }
}

function search() {
  query.current = 1
  load()
}

function resetFilters() {
  Object.assign(filters, { keyword: '', categoryId: '', publishStatus: '', auditStatus: '' })
  search()
}

function handleSizeChange() {
  query.current = 1
  load()
}

function categoryName(id) {
  const item = categories.value.find(c => c.id === id)
  return item ? item.name : `#${id ?? '-'}`
}

function auditText(status) {
  return { 0: '待审核', 1: '已通过', 2: '已驳回' }[status] || '未知'
}

function auditTagType(status) {
  return { 0: 'warning', 1: 'success', 2: 'danger' }[status] || 'info'
}

function onSupplierChange(id) {
  const supplier = suppliers.value.find(s => s.id === id)
  if (supplier) form.supname = supplier.supname
}

function openCreate() {
  Object.assign(form, emptyForm())
  if (suppliers.value.length) {
    form.supid = suppliers.value[0].id
    form.supname = suppliers.value[0].supname
  }
  if (categories.value.length) form.categoryId = categories.value[0].id
  dialog.id = null
  dialog.visible = true
}

function openEdit(row) {
  Object.assign(form, {
    id: row.id,
    proname: row.proname,
    supid: row.supid,
    supname: row.supname,
    categoryId: row.categoryId,
    delivery: row.delivery,
    price: Number(row.price),
    stock: row.stock,
    unit: row.unit,
    descript: row.descript,
    picurl: row.picurl,
    publishStatus: row.publishStatus,
    auditStatus: row.auditStatus
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
    proname: form.proname.trim(),
    supid: form.supid,
    supname: form.supname,
    categoryId: form.categoryId,
    delivery: form.delivery,
    price: form.price,
    stock: form.stock,
    unit: form.unit,
    descript: form.descript,
    picurl: form.picurl,
    publishStatus: form.publishStatus,
    auditStatus: form.auditStatus
  }
  saving.value = true
  try {
    if (dialog.id) {
      await updateProduct(dialog.id, payload)
      ElMessage.success('商品已更新')
    } else {
      await saveProduct(payload)
      ElMessage.success('商品已创建')
    }
    dialog.visible = false
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  } finally {
    saving.value = false
  }
}

async function togglePublish(row) {
  const next = row.publishStatus === 1 ? 0 : 1
  try {
    await publishProduct(row.id, next)
    ElMessage.success(next === 1 ? '商品已上架' : '商品已下架')
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  }
}

async function audit(row, status) {
  try {
    await ElMessageBox.confirm(
      `确定将「${row.proname}」标记为${status === 1 ? '审核通过' : '审核驳回'}吗？`,
      '审核商品',
      { type: 'warning' }
    )
  } catch (e) {
    return
  }
  try {
    await auditProduct(row.id, status)
    ElMessage.success('审核状态已更新')
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除商品「${row.proname}」吗？`, '删除商品', { type: 'warning' })
  } catch (e) {
    return
  }
  try {
    await deleteProduct(row.id)
    ElMessage.success('商品已删除')
    if (rows.value.length === 1 && query.current > 1) query.current -= 1
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  }
}

onMounted(() => {
  loadCategories()
  loadSuppliers()
  load()
})
</script>
