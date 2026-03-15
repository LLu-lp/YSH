<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h2>平台概览</h2>
      <p>欢迎回来，管理员</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon blue"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg></div>
        <div class="stat-info">
          <p class="stat-label">用户总数</p>
          <p class="stat-value">{{ stats.totalUsers || 0 }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg></div>
        <div class="stat-info">
          <p class="stat-label">商家总数</p>
          <p class="stat-value">{{ stats.totalMerchants || 0 }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/></svg></div>
        <div class="stat-info">
          <p class="stat-label">商品总数</p>
          <p class="stat-value">{{ stats.totalProducts || 0 }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg></div>
        <div class="stat-info">
          <p class="stat-label">订单总数</p>
          <p class="stat-value">{{ stats.totalOrders || 0 }}</p>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card large">
        <div class="chart-header">
          <h3>近7日销售趋势</h3>
        </div>
        <div class="chart-placeholder">
          <div class="bar-chart">
            <div v-for="(item, index) in salesTrend" :key="index" class="bar-item">
              <div class="bar" :style="{ height: getBarHeight(item.amount) + '%' }"></div>
              <p class="bar-label">{{ formatDateLabel(item.date) }}</p>
              <p class="bar-value">¥{{ formatAmount(item.amount) }}</p>
            </div>
          </div>
        </div>
      </div>
      <div class="chart-card small">
        <div class="chart-header">
          <h3>待处理事项</h3>
        </div>
        <div class="todo-list">
          <div v-for="item in todoList" :key="item.type" class="todo-item" @click="handleTodo(item)">
            <div class="todo-icon" :class="item.type">
              <svg v-if="item.type === 'product_audit'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/></svg>
              <svg v-else-if="item.type === 'merchant_audit'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            </div>
            <div class="todo-info">
              <p class="todo-title">{{ item.title }}</p>
              <p class="todo-count">{{ item.count }} 项待处理</p>
            </div>
            <span class="todo-arrow">→</span>
          </div>
          <div v-if="todoList.length === 0" class="empty-todo">
            <p>暂无待处理事项</p>
          </div>
        </div>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-box">
        <h4>平台总销售额</h4>
        <p class="big-number">¥{{ formatPrice(stats.totalSales) }}</p>
      </div>
      <div class="stat-box">
        <h4>待审核商品</h4>
        <p class="big-number warn">{{ stats.pendingProductAudits || 0 }}</p>
      </div>
      <div class="stat-box">
        <h4>待审核商家</h4>
        <p class="big-number warn">{{ stats.pendingMerchantAudits || 0 }}</p>
      </div>
      <div class="stat-box">
        <h4>待处理退款</h4>
        <p class="big-number danger">{{ stats.pendingRefunds || 0 }}</p>
      </div>
    </div>

    <div class="recent-card">
      <div class="card-header">
        <h3>最新订单</h3>
        <router-link to="/admin/orders" class="view-all">查看全部</router-link>
      </div>
      <table class="data-table">
        <thead>
          <tr>
            <th>订单编号</th>
            <th>用户</th>
            <th>店铺</th>
            <th>金额</th>
            <th>状态</th>
            <th>下单时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in recentOrders" :key="order.id">
            <td class="order-no">{{ order.orderNo }}</td>
            <td>{{ order.username || '-' }}</td>
            <td>{{ order.shopName || '-' }}</td>
            <td class="amount">¥{{ order.payAmount?.toFixed(2) || '0.00' }}</td>
            <td><span class="status-tag" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span></td>
            <td class="time">{{ formatDateTime(order.createTime) }}</td>
            <td>
              <button class="btn-action" @click="$router.push('/admin/orders')">查看</button>
            </td>
          </tr>
          <tr v-if="recentOrders.length === 0">
            <td colspan="7" style="text-align: center; color: #a8a29e;">暂无订单数据</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { adminGetOrders, getAdminSalesTrend, getAdminStats, getAdminTodoList } from '@/api'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const stats = ref({
  totalUsers: 0,
  totalMerchants: 0,
  totalProducts: 0,
  totalOrders: 0,
  totalSales: 0,
  pendingProductAudits: 0,
  pendingMerchantAudits: 0,
  pendingRefunds: 0
})
const salesTrend = ref([])
const todoList = ref([])
const recentOrders = ref([])

const maxAmount = computed(() => {
  if (salesTrend.value.length === 0) return 1
  return Math.max(...salesTrend.value.map(item => item.amount || 0), 1)
})

const getBarHeight = (amount) => {
  if (!amount || maxAmount.value === 0) return 5
  return Math.max(5, (amount / maxAmount.value) * 100)
}

const formatDateLabel = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return days[date.getDay()]
}

const formatAmount = (amount) => {
  if (!amount) return '0'
  if (amount >= 1000) {
    return (amount / 1000).toFixed(1) + 'k'
  }
  return amount.toFixed(0)
}

const formatPrice = (price) => {
  if (!price) return '0.00'
  return price.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

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
    4: '已取消',
    5: '退款中'
  }
  return texts[status] || '未知'
}

const handleTodo = (item) => {
  router.push(item.link)
}

const loadData = async () => {
  try {
    const [statsRes, trendRes, todoRes, ordersRes] = await Promise.all([
      getAdminStats(),
      getAdminSalesTrend({ days: 7 }),
      getAdminTodoList(),
      adminGetOrders({ page: 1, pageSize: 5 })
    ])

    if (statsRes.code === 200) {
      stats.value = statsRes.data
    }

    if (trendRes.code === 200) {
      salesTrend.value = trendRes.data || []
    }

    if (todoRes.code === 200) {
      todoList.value = todoRes.data || []
    }

    if (ordersRes.code === 200 && ordersRes.data?.content) {
      recentOrders.value = ordersRes.data.content
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard-page { padding: 28px 32px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.page-header { margin-bottom: 24px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0 0 4px; }
.page-header p { font-size: 14px; color: #78716c; margin: 0; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px; }
.stat-card { background: white; border-radius: 12px; padding: 20px; display: flex; align-items: center; gap: 16px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.stat-icon { width: 44px; height: 44px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-icon svg { width: 22px; height: 22px; }
.stat-icon.blue { background: #eff6ff; color: #3b82f6; }
.stat-icon.orange { background: #fff7ed; color: #f97316; }
.stat-icon.green { background: #f0fdf4; color: #10b981; }
.stat-icon.purple { background: #faf5ff; color: #8b5cf6; }
.stat-label { font-size: 12px; color: #a8a29e; margin: 0 0 4px; }
.stat-value { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0; }
.charts-row { display: grid; grid-template-columns: 1fr 340px; gap: 16px; margin-bottom: 20px; }
.chart-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.chart-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.chart-header h3 { font-size: 15px; font-weight: 600; color: #1c1917; margin: 0; }
.bar-chart { display: flex; align-items: flex-end; gap: 12px; height: 180px; padding-top: 20px; }
.bar-item { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 6px; height: 100%; justify-content: flex-end; }
.bar { width: 100%; background: #3b82f6; border-radius: 4px 4px 0 0; transition: height 0.3s; min-height: 4px; }
.bar-label { font-size: 12px; color: #a8a29e; }
.bar-value { font-size: 11px; color: #78716c; }
.todo-list { display: flex; flex-direction: column; gap: 12px; }
.todo-item { display: flex; align-items: center; gap: 12px; padding: 12px; background: #f5f5f4; border-radius: 8px; cursor: pointer; transition: all 0.15s; }
.todo-item:hover { background: #e7e5e4; }
.todo-icon { width: 36px; height: 36px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.todo-icon svg { width: 18px; height: 18px; }
.todo-icon.product_audit { background: #fff7ed; color: #f97316; }
.todo-icon.merchant_audit { background: #eff6ff; color: #3b82f6; }
.todo-icon.refund_handle { background: #fef2f2; color: #ef4444; }
.todo-info { flex: 1; }
.todo-title { font-size: 13px; font-weight: 500; color: #1c1917; margin: 0 0 2px; }
.todo-count { font-size: 12px; color: #78716c; margin: 0; }
.todo-arrow { color: #a8a29e; font-size: 14px; }
.empty-todo { text-align: center; padding: 20px; color: #a8a29e; }
.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px; }
.stat-box { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); text-align: center; }
.stat-box h4 { font-size: 13px; font-weight: 500; color: #78716c; margin: 0 0 8px; }
.big-number { font-size: 28px; font-weight: 700; color: #1c1917; margin: 0; }
.big-number.warn { color: #f59e0b; }
.big-number.danger { color: #ef4444; }
.recent-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.card-header h3 { font-size: 15px; font-weight: 600; color: #1c1917; margin: 0; }
.view-all { font-size: 13px; color: #3b82f6; text-decoration: none; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th { text-align: left; padding: 10px 12px; color: #a8a29e; font-weight: 500; border-bottom: 1px solid #f5f5f4; white-space: nowrap; }
.data-table td { padding: 12px 12px; color: #44403c; border-bottom: 1px solid #f5f5f4; }
.data-table tr:last-child td { border-bottom: none; }
.order-no { font-family: monospace; color: #78716c; font-size: 12px; }
.amount { font-weight: 600; color: #3b82f6; }
.time { color: #a8a29e; font-size: 12px; }
.status-tag { padding: 3px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.status-tag.warning { background: #fff7ed; color: #f97316; }
.status-tag.success { background: #f0fdf4; color: #10b981; }
.status-tag.pending { background: #fefce8; color: #ca8a04; }
.status-tag.info { background: #eff6ff; color: #3b82f6; }
.status-tag.danger { background: #fef2f2; color: #ef4444; }
.status-tag.default { background: #f5f5f4; color: #78716c; }
.btn-action { padding: 5px 12px; background: #eff6ff; color: #3b82f6; border: 1px solid #bfdbfe; border-radius: 6px; font-size: 12px; cursor: pointer; transition: all 0.15s; }
.btn-action:hover { background: #3b82f6; color: white; }
</style>
