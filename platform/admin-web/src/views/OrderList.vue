<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>订单管理</h2>
        <div class="sub">查看订单详情并为已支付订单发货</div>
      </div>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="订单号">
          <el-input v-model="filters.orderid" clearable placeholder="按订单号精确查询" style="width: 220px" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.state" clearable placeholder="全部状态" style="width: 140px">
            <el-option v-for="item in stateOptions" :key="item.value" :label="item.label" :value="item.value" />
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
        <el-table-column prop="orderid" label="订单号" width="205" />
        <el-table-column label="商品" min-width="220">
          <template #default="{ row }">
            <div class="cell-product">
              <img v-if="row.picurl" :src="row.picurl" alt="" />
              <div class="name">
                {{ row.proname }}
                <div class="muted">× {{ row.num }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="收货人" width="140">
          <template #default="{ row }">
            {{ row.receiverName }}
            <div class="muted">{{ row.receiverTel }}</div>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="110">
          <template #default="{ row }"><span class="money">{{ formatMoney(row.pay) }}</span></template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="stateTagType(row.state)" size="small">{{ row.stateText || keyStateText(row.state) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="165">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button v-if="row.state === 'paid'" link type="success" @click="openShip(row)">发货</el-button>
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

    <el-drawer v-model="drawer.visible" title="订单详情" size="540px">
      <div v-if="detail" v-loading="drawer.loading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号" :span="2">{{ detail.orderid }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="stateTagType(detail.state)" size="small">{{ detail.stateText || keyStateText(detail.state) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">
            <span class="money">{{ formatMoney(detail.pay) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="用户 ID">{{ detail.userid }}</el-descriptions-item>
          <el-descriptions-item label="供应商">{{ detail.supname }}</el-descriptions-item>
          <el-descriptions-item label="商品" :span="2">{{ detail.proname }}</el-descriptions-item>
          <el-descriptions-item label="单价">{{ formatMoney(detail.price) }}</el-descriptions-item>
          <el-descriptions-item label="数量">{{ detail.num }}</el-descriptions-item>
          <el-descriptions-item label="快递信息" :span="2">{{ detail.express || '未发货' }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatTime(detail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ formatTime(detail.payTime) }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ formatTime(detail.shipTime) }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ formatTime(detail.completeTime) }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.cancelTime" label="取消时间" :span="2">{{ formatTime(detail.cancelTime) }}</el-descriptions-item>
        </el-descriptions>

        <div class="drawer-section">
          <h4>收货信息</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="收货人">{{ detail.receiverName }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detail.receiverTel }}</el-descriptions-item>
            <el-descriptions-item label="收货地址">{{ detail.receiverAddress }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-if="detail.picurl" class="drawer-section">
          <el-image :src="detail.picurl" :preview-src-list="[detail.picurl]" preview-teleported fit="cover" style="width: 160px; height: 120px; border-radius: 8px" />
        </div>

        <div v-if="detail.state === 'paid'" class="drawer-section">
          <el-button type="primary" @click="openShip(detail)">立即发货</el-button>
        </div>
      </div>
    </el-drawer>

    <el-dialog v-model="shipDialog.visible" title="订单发货" width="480px" :close-on-click-modal="false">
      <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules" label-width="90px">
        <el-form-item label="订单号">
          <span>{{ shipDialog.orderid }}</span>
        </el-form-item>
        <el-form-item label="快递信息" prop="express">
          <el-input v-model="shipForm.express" maxlength="128" placeholder="如：顺丰速运 SF1234567890" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="shipDialog.saving" @click="submitShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderDetail, getOrderPage, shipOrder } from '../api/user'
import { formatMoney, formatTime } from '../utils/format'

const stateOptions = [
  { value: 'created', label: '待付款' },
  { value: 'paid', label: '待发货' },
  { value: 'shipped', label: '待收货' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' }
]

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const detail = ref(null)
const shipFormRef = ref()

const query = reactive({ current: 1, limit: 10 })
const filters = reactive({ orderid: '', state: '' })
const drawer = reactive({ visible: false, loading: false })
const shipDialog = reactive({ visible: false, saving: false, orderid: '' })
const shipForm = reactive({ express: '' })
const shipRules = {
  express: [{ required: true, message: '请输入快递信息', trigger: 'blur' }]
}

function cleanParams() {
  const params = { current: query.current, limit: query.limit, ...filters }
  Object.keys(params).forEach(key => {
    if (params[key] === '' || params[key] === null || params[key] === undefined) delete params[key]
  })
  return params
}

function keyStateText(state) {
  const item = stateOptions.find(s => s.value === state)
  return item ? item.label : state || '-'
}

function stateTagType(state) {
  return {
    created: 'warning',
    paid: 'primary',
    shipped: 'info',
    completed: 'success',
    cancelled: 'danger'
  }[state] || 'info'
}

async function load() {
  loading.value = true
  try {
    const data = await getOrderPage(cleanParams())
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
  Object.assign(filters, { orderid: '', state: '' })
  search()
}

function handleSizeChange() {
  query.current = 1
  load()
}

async function openDetail(row) {
  drawer.visible = true
  drawer.loading = true
  detail.value = null
  try {
    detail.value = await getOrderDetail(row.orderid)
  } catch (e) {
    // 错误提示由拦截器处理
  } finally {
    drawer.loading = false
  }
}

function openShip(row) {
  shipDialog.orderid = row.orderid
  shipForm.express = ''
  shipDialog.visible = true
}

async function submitShip() {
  if (!shipFormRef.value) return
  try {
    await shipFormRef.value.validate()
  } catch (e) {
    return
  }
  shipDialog.saving = true
  try {
    await shipOrder(shipDialog.orderid, { express: shipForm.express.trim() })
    ElMessage.success('订单已发货')
    shipDialog.visible = false
    if (drawer.visible && detail.value && detail.value.orderid === shipDialog.orderid) {
      detail.value = await getOrderDetail(shipDialog.orderid)
    }
    load()
  } catch (e) {
    // 错误提示由拦截器处理
  } finally {
    shipDialog.saving = false
  }
}

onMounted(load)
</script>
