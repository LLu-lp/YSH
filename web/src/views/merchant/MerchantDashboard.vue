<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h2>数据概览</h2>
      <p>欢迎回来，{{ shopInfo?.name || '商家' }}</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon orange"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg></div>
        <div class="stat-info">
          <p class="stat-label">今日销售额</p>
          <p class="stat-value">¥{{ stats.todaySales?.toLocaleString() || '0' }}</p>
          <p class="stat-trend">今日实时数据</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon blue"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg></div>
        <div class="stat-info">
          <p class="stat-label">今日订单数</p>
          <p class="stat-value">{{ stats.todayOrders || 0 }}</p>
          <p class="stat-trend">今日实时数据</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/></svg></div>
        <div class="stat-info">
          <p class="stat-label">在售商品数</p>
          <p class="stat-value">{{ stats.totalProducts || 0 }}</p>
          <p class="stat-trend">商品总数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon red"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07A19.5 19.5 0 013.07 9.81 19.79 19.79 0 01.14 1.24 2 2 0 012.11 0h3a2 2 0 012 1.72c.127.96.361 1.903.7 2.81a2 2 0 01-.45 2.11L6.09 7.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0122 16.92z"/></svg></div>
        <div class="stat-info">
          <p class="stat-label">待处理订单</p>
          <p class="stat-value">{{ stats.pendingOrders || 0 }}</p>
          <p class="stat-trend warn">需要及时处理</p>
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
          <h3>商品分类占比</h3>
        </div>
        <div class="category-list">
          <div v-for="(cat, index) in categoryData" :key="cat.name" class="category-item">
            <div class="cat-info">
              <span class="cat-dot" :style="{ background: colors[index % colors.length] }"></span>
              <span>{{ cat.name }}</span>
            </div>
            <div class="cat-bar-wrap">
              <div class="cat-bar" :style="{ width: cat.ratio + '%', background: colors[index % colors.length] }"></div>
            </div>
            <span class="cat-ratio">{{ cat.ratio }}%</span>
          </div>
        </div>
      </div>
    </div>

    <div class="recent-card">
      <div class="card-header">
        <h3>最新订单</h3>
        <router-link to="/merchant/orders" class="view-all">查看全部</router-link>
      </div>
      <table class="data-table">
        <thead>
          <tr>
            <th>订单编号</th>
            <th>商品</th>
            <th>买家</th>
            <th>金额</th>
            <th>状态</th>
            <th>下单时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in recentOrders" :key="order.id">
            <td class="order-no">{{ order.orderNo }}</td>
            <td>{{ order.orderItems?.[0]?.productName || '-' }}</td>
            <td>{{ order.receiverName }}</td>
            <td class="amount">¥{{ order.payAmount?.toFixed(2) || '0.00' }}</td>
            <td><span class="status-tag" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span></td>
            <td class="time">{{ formatDateTime(order.createTime) }}</td>
            <td>
              <button class="btn-action" @click="$router.push('/merchant/orders')">查看</button>
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
import { ref, onMounted, computed } from 'vue'
import { getMerchantOverview, getMerchantSalesTrend, getMerchantCategoryRatio, getMerchantOrders, getShopInfo } from '@/api'

const shopInfo = ref(null)
const stats = ref({
  todaySales: 0,
  todayOrders: 0,
  totalProducts: 0,
  pendingOrders: 0
})
const salesTrend = ref([])
const categoryData = ref([])
const recentOrders = ref([])
const colors = ['#f97316', '#3b82f6', '#10b981', '#f59e0b', '#8b5cf6', '#ec4899']

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

const loadData = async () => {
  try {
    const [overviewRes, trendRes, categoryRes, ordersRes] = await Promise.all([
      getMerchantOverview(),
      getMerchantSalesTrend({ days: 7 }),
      getMerchantCategoryRatio(),
      getMerchantOrders({ page: 1, pageSize: 5 })
    ])

    if (overviewRes.code === 200) {
      stats.value = overviewRes.data
    }

    if (trendRes.code === 200) {
      salesTrend.value = trendRes.data || []
    }

    if (categoryRes.code === 200) {
      let data = categoryRes.data
      // 处理不同的响应格式
      if (Array.isArray(data)) {
        data = data
      } else if (data?.data && Array.isArray(data.data)) {
        data = data.data
      } else if (data?.content && Array.isArray(data.content)) {
        data = data.content
      }
      
      if (Array.isArray(data) && data.length > 0) {
        const total = data.reduce((sum, item) => sum + (item.value || item.count || 0), 0)
        categoryData.value = data.map(item => ({
          name: item.name || item.categoryName || '未分类',
          ratio: total > 0 ? Math.round(((item.value || item.count || 0) / total) * 100) : 0
      }))
      }
    }

    if (ordersRes.code === 200 && ordersRes.data?.content) {
      recentOrders.value = ordersRes.data.content
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const loadShopInfo = async () => {
  try {
    const res = await getShopInfo()
    if (res.code === 200) {
      shopInfo.value = res.data
    }
  } catch (error) {
    console.error('加载店铺信息失败:', error)
  }
}

onMounted(() => {
  loadData()
  loadShopInfo()
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
.stat-icon.orange { background: #fff7ed; color: #f97316; }
.stat-icon.blue { background: #eff6ff; color: #3b82f6; }
.stat-icon.green { background: #f0fdf4; color: #10b981; }
.stat-icon.red { background: #fef2f2; color: #ef4444; }
.stat-label { font-size: 12px; color: #a8a29e; margin: 0 0 4px; }
.stat-value { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0 0 4px; }
.stat-trend { font-size: 12px; margin: 0; color: #a8a29e; }
.stat-trend.warn { color: #f59e0b; }
.charts-row { display: grid; grid-template-columns: 1fr 340px; gap: 16px; margin-bottom: 20px; }
.chart-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.chart-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.chart-header h3 { font-size: 15px; font-weight: 600; color: #1c1917; margin: 0; }
.bar-chart { display: flex; align-items: flex-end; gap: 12px; height: 180px; padding-top: 20px; }
.bar-item { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 6px; height: 100%; justify-content: flex-end; }
.bar { width: 100%; background: #f97316; border-radius: 4px 4px 0 0; transition: height 0.3s; min-height: 4px; }
.bar-label { font-size: 12px; color: #a8a29e; }
.bar-value { font-size: 11px; color: #78716c; }
.category-list { display: flex; flex-direction: column; gap: 14px; }
.category-item { display: flex; align-items: center; gap: 10px; }
.cat-info { display: flex; align-items: center; gap: 6px; width: 80px; flex-shrink: 0; font-size: 13px; color: #44403c; }
.cat-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.cat-bar-wrap { flex: 1; background: #f5f5f4; border-radius: 4px; height: 8px; overflow: hidden; }
.cat-bar { height: 100%; border-radius: 4px; transition: width 0.3s; }
.cat-ratio { font-size: 13px; font-weight: 600; color: #44403c; width: 36px; text-align: right; }
.recent-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.card-header h3 { font-size: 15px; font-weight: 600; color: #1c1917; margin: 0; }
.view-all { font-size: 13px; color: #f97316; text-decoration: none; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th { text-align: left; padding: 10px 12px; color: #a8a29e; font-weight: 500; border-bottom: 1px solid #f5f5f4; white-space: nowrap; }
.data-table td { padding: 12px 12px; color: #44403c; border-bottom: 1px solid #f5f5f4; }
.data-table tr:last-child td { border-bottom: none; }
.order-no { font-family: monospace; color: #78716c; font-size: 12px; }
.amount { font-weight: 600; color: #f97316; }
.time { color: #a8a29e; font-size: 12px; }
.status-tag { padding: 3px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.status-tag.warning { background: #fff7ed; color: #f97316; }
.status-tag.success { background: #f0fdf4; color: #10b981; }
.status-tag.pending { background: #fefce8; color: #ca8a04; }
.status-tag.info { background: #eff6ff; color: #3b82f6; }
.status-tag.danger { background: #fef2f2; color: #ef4444; }
.status-tag.default { background: #f5f5f4; color: #78716c; }
.btn-action { padding: 5px 12px; background: #fff7ed; color: #f97316; border: 1px solid #fed7aa; border-radius: 6px; font-size: 12px; cursor: pointer; transition: all 0.15s; }
.btn-action:hover { background: #f97316; color: white; }
</style>
