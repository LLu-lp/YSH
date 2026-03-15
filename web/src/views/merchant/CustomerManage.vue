<template>
  <div class="customers-page">
    <div class="page-header">
      <h2>用户管理</h2>
      <p class="subtitle">查看购买过您商品的用户信息</p>
    </div>

    <div class="filter-bar">
      <input v-model="filters.keyword" type="text" placeholder="搜索用户名..." @keyup.enter="handleSearch" />
      <button class="btn-search" @click="handleSearch">搜索</button>
      <button class="btn-reset" @click="handleReset">重置</button>
    </div>

    <div class="table-card" v-loading="loading">
      <table class="data-table">
        <thead>
          <tr>
            <th>用户ID</th>
            <th>用户名</th>
            <th>订单数</th>
            <th>消费金额</th>
            <th>最后购买时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="customer in customers" :key="customer.userId">
            <td class="user-id">{{ customer.userId }}</td>
            <td class="username">{{ customer.username }}</td>
            <td class="order-count">{{ customer.totalOrders }}</td>
            <td class="amount">¥{{ customer.totalAmount?.toFixed(2) || '0.00' }}</td>
            <td class="time">{{ formatDateTime(customer.lastOrderTime) }}</td>
            <td>
              <button class="btn-action" @click="viewCustomerOrders(customer)">查看订单</button>
            </td>
          </tr>
          <tr v-if="customers.length === 0 && !loading">
            <td colspan="6" style="text-align: center; color: #a8a29e; padding: 40px;">暂无用户数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <button @click="page > 1 && page--" :disabled="page === 1">上一页</button>
        <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
        <button @click="page < totalPages && page++" :disabled="page >= totalPages">下一页</button>
      </div>
    </div>

    <!-- 用户订单详情弹窗 -->
    <div v-if="showOrderModal" class="modal-mask" @click.self="showOrderModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>用户订单 - {{ selectedCustomer?.username }}</h3>
          <button @click="showOrderModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div v-if="customerOrders.length > 0" class="orders-list">
            <div v-for="order in customerOrders" :key="order.id" class="order-item">
              <div class="order-header">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-time">{{ formatDateTime(order.createTime) }}</span>
                <span class="status-tag" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
              </div>
              <div class="order-detail">
                <div class="detail-row">
                  <span class="label">商品数量：</span>
                  <span>{{ (order.orderItems || []).length }} 件</span>
                </div>
                <div class="detail-row">
                  <span class="label">订单金额：</span>
                  <span class="amount">¥{{ order.payAmount?.toFixed(2) || '0.00' }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">收货地址：</span>
                  <span>{{ order.receiverAddress }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty">
            <p>该用户暂无订单</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMerchantCustomers, getMerchantOrders } from '@/api'

const loading = ref(false)
const page = ref(1)
const pageSize = 10
const allCustomers = ref([])  // 全量数据
const filters = ref({ keyword: '' })

// 前端关键词过滤
const filteredCustomers = computed(() => {
  const kw = filters.value.keyword.trim().toLowerCase()
  if (!kw) return allCustomers.value
  return allCustomers.value.filter(c => c.username && c.username.toLowerCase().includes(kw))
})

// 当前页数据
const customers = computed(() => {
  const start = (page.value - 1) * pageSize
  return filteredCustomers.value.slice(start, start + pageSize)
})

const total = computed(() => filteredCustomers.value.length)
const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)

const showOrderModal = ref(false)
const selectedCustomer = ref(null)
const customerOrders = ref([])

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

const getStatusClass = (status) => {
  const classes = {
    0: 'pending',
    1: 'warning',
    2: 'info',
    3: 'success',
    4: 'default',
    5: 'danger'
  }
  return classes[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    0: '待付款',
    1: '待发货',
    2: '已发货',
    3: '已完成',
    4: '已退款',
    5: '退款中'
  }
  return texts[status] || '未知'
}

const loadCustomers = async () => {
  loading.value = true
  try {
    // 一次性加载全部，前端做过滤和分页
    const res = await getMerchantCustomers({ page: 1, pageSize: 1000 })
    if (res.code === 200 && res.data) {
      if (res.data.content) {
        allCustomers.value = res.data.content
      } else if (Array.isArray(res.data)) {
        allCustomers.value = res.data
      }
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1  // 搜索时回到第一页
}

const handleReset = () => {
  filters.value.keyword = ''
  page.value = 1
}

const viewCustomerOrders = async (customer) => {
  selectedCustomer.value = customer
  try {
    const res = await getMerchantOrders({ page: 1, pageSize: 100 })
    if (res.code === 200 && res.data) {
      let allOrders = Array.isArray(res.data) ? res.data : res.data.content || []
      customerOrders.value = allOrders.filter(order => order.userId === customer.userId)
    }
  } catch (error) {
    console.error('加载用户订单失败:', error)
    ElMessage.error('加载用户订单失败')
  }
  showOrderModal.value = true
}

onMounted(() => loadCustomers())
</script>

<style scoped>
.customers-page { padding: 28px 32px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.page-header { margin-bottom: 24px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0 0 4px; }
.subtitle { font-size: 14px; color: #78716c; margin: 0; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.filter-bar input { padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; min-width: 220px; outline: none; }
.filter-bar input:focus { border-color: #f97316; }
.btn-search { padding: 9px 18px; background: #f97316; color: white; border: none; border-radius: 8px; font-size: 13px; cursor: pointer; font-weight: 600; }
.btn-reset { padding: 9px 18px; background: white; color: #78716c; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; cursor: pointer; }
.table-card { background: white; border-radius: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th { text-align: left; padding: 14px 16px; color: #a8a29e; font-weight: 500; background: #fafaf9; border-bottom: 1px solid #f5f5f4; }
.data-table td { padding: 12px 16px; color: #44403c; border-bottom: 1px solid #f5f5f4; }
.user-id { font-family: monospace; font-size: 12px; color: #78716c; }
.username { font-weight: 500; }
.order-count { text-align: center; font-weight: 600; color: #f97316; }
.amount { font-weight: 600; color: #f97316; }
.time { color: #a8a29e; font-size: 12px; }
.btn-action { padding: 6px 14px; background: #fff7ed; color: #f97316; border: 1.5px solid #fed7aa; border-radius: 6px; font-size: 12px; cursor: pointer; transition: all 0.15s; }
.btn-action:hover { background: #f97316; color: white; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 12px; padding: 14px 16px; border-top: 1px solid #f5f5f4; font-size: 13px; color: #78716c; }
.pagination button { padding: 6px 14px; border: 1.5px solid #e7e5e4; border-radius: 6px; background: white; font-size: 13px; cursor: pointer; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: white; border-radius: 14px; width: 600px; max-height: 80vh; display: flex; flex-direction: column; overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; border-bottom: 1px solid #f5f5f4; }
.modal-header h3 { font-size: 16px; font-weight: 600; color: #1c1917; margin: 0; }
.modal-close { background: none; border: none; font-size: 22px; color: #a8a29e; cursor: pointer; }
.modal-body { flex: 1; overflow-y: auto; padding: 20px 24px; }
.orders-list { display: flex; flex-direction: column; gap: 12px; }
.order-item { background: #fafaf9; border-radius: 8px; padding: 12px; border: 1px solid #f5f5f4; }
.order-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.order-no { font-size: 13px; font-weight: 600; color: #1c1917; font-family: monospace; }
.order-time { font-size: 12px; color: #a8a29e; }
.status-tag { padding: 2px 8px; border-radius: 12px; font-size: 11px; font-weight: 500; }
.status-tag.pending { background: #fefce8; color: #ca8a04; }
.status-tag.warning { background: #fff7ed; color: #f97316; }
.status-tag.info { background: #eff6ff; color: #3b82f6; }
.status-tag.success { background: #f0fdf4; color: #10b981; }
.status-tag.danger { background: #fef2f2; color: #ef4444; }
.status-tag.default { background: #f5f5f4; color: #a8a29e; }
.order-detail { display: flex; flex-direction: column; gap: 6px; }
.detail-row { display: flex; justify-content: space-between; font-size: 12px; }
.detail-row .label { color: #a8a29e; }
.detail-row .amount { color: #f97316; font-weight: 600; }
.empty { text-align: center; padding: 40px 20px; color: #a8a29e; }
</style>

