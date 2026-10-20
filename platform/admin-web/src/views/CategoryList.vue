<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>分类管理</h2>
        <div class="sub">分类下仍有商品时不可删除</div>
      </div>
      <el-button type="primary" @click="openCreate">新增分类</el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图标" width="90">
          <template #default="{ row }">
            <el-image
              v-if="row.icon"
              :src="row.icon"
              :preview-src-list="[row.icon]"
              preview-teleported
              fit="cover"
              style="width: 44px; height: 44px; border-radius: 6px"
            />
            <span v-else class="muted">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="分类名称" min-width="140" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.id ? '编辑分类' : '新增分类'" width="480px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="84px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" maxlength="32" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标地址">
          <el-input v-model="form.icon" maxlength="512" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :precision="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
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
import { deleteCategory, getCategories, saveCategory, updateCategory } from '../api/product'
import { formatTime } from '../utils/format'

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const formRef = ref()

const dialog = reactive({ visible: false, id: null })
const form = reactive(emptyForm())

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

function emptyForm() {
  return { name: '', icon: '', sortOrder: 0, status: 1 }
}

async function load() {
  loading.value = true
  try {
    rows.value = (await getCategories()) || []
  } catch (e) {
    // 错误提示由拦截器处理
  } finally {
    loading.value = false
  }
}

function openCreate() {
  Object.assign(form, emptyForm())
  dialog.id = null
  dialog.visible = true
}

function openEdit(row) {
  Object.assign(form, {
    name: row.name,
    icon: row.icon || '',
    sortOrder: row.sortOrder || 0,
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
    name: form.name.trim(),
    icon: form.icon,
    sortOrder: form.sortOrder,
    status: form.status
  }
  saving.value = true
  try {
    if (dialog.id) {
      await updateCategory(dialog.id, payload)
      ElMessage.success('分类已更新')
    } else {
      await saveCategory(payload)
      ElMessage.success('分类已创建')
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
    await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '删除分类', { type: 'warning' })
  } catch (e) {
    return
  }
  try {
    await deleteCategory(row.id)
    ElMessage.success('分类已删除')
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  }
}

onMounted(load)
</script>
