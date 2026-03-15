<template>
  <div class="orders-page">
    <div class="page-header">
      <h2 class="page-title">我的订单</h2>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="待收货" name="2" />
      <el-tab-pane label="已完成" name="3" />
      <el-tab-pane label="已取消" name="4" />
      <el-tab-pane label="售后/退款" name="5" />
    </el-tabs>

    <div v-loading="loading">
      <div v-if="orders.length > 0">
        <div class="order-card" v-for="order in orders" :key="order.id">
          <div class="order-header">
            <span class="shop-name" v-if="order.shopName">{{ order.shopName }}</span>
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-date">{{ formatDateTime(order.createTime) }}</span>
            <span class="order-status" :class="statusClass(order.status)">{{ statusText(order.status) }}</span>
          </div>
          <div class="order-items">
            <div class="order-item" v-for="item in order.orderItems || []" :key="item.id" @click="$router.push(`/user/products/${item.productId}`)">>
              <img :src="item.productImage || '/placeholder.svg?height=70&width=70'" :alt="item.productName" />
              <div class="item-info">
                <p class="item-name">{{ item.productName }}</p>
                <p class="item-sku">{{ item.spec || '' }}</p>
              </div>
              <div class="item-price">¥{{ item.price }} × {{ item.quantity }}</div>
            </div>
          </div>
          <div class="order-footer">
            <div class="order-total">
              共 {{ (order.orderItems || []).length }} 件，合计：<span class="total-price">¥{{ order.payAmount || order.totalAmount }}</span>
              <span class="shipping-text">（含运费 ¥0.00）</span>
            </div>
            <div class="order-actions">
              <template v-if="order.status === 0">
                <el-button size="small" @click="handleCancel(order.id)">取消订单</el-button>
                <el-button type="primary" size="small" @click="handlePay(order.id)">立即付款</el-button>
              </template>
              <template v-if="order.status === 2">
                <el-button size="small" @click="handleRefund(order.id)">申请售后</el-button>
                <el-button type="primary" size="small" @click="handleConfirm(order.id)">确认收货</el-button>
              </template>
              <el-button size="small" @click="$router.push(`/user/orders/${order.id}`)">查看详情</el-button>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无相关订单" :image-size="120">
        <el-button type="primary" @click="$router.push('/user/products')">去购物</el-button>
      </el-empty>
    </div>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="page"
        :total="total"
        :page-size="10"
        layout="total, prev, pager, next"
        @change="loadOrders"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrders, payOrder, confirmOrder, cancelOrder, applyRefund } from '@/api'

const activeTab = ref('all')
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const orders = ref([])

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

const statusText = (s) => ({ 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消', 5: '退款中' }[s] || '未知')
const statusClass = (s) => ({ 0: 'status-pending', 1: 'status-shipped', 2: 'status-delivering', 3: 'status-done', 4: 'status-cancel', 5: 'status-refund' }[s])

const handleTabChange = (tab) => { 
  activeTab.value = tab.paneName
  page.value = 1
  loadOrders() 
}

const handlePay = async (id) => {
  await ElMessageBox.confirm('确认支付该订单？（模拟支付）', '支付确认', { type: 'warning' })
  try {
    await payOrder(id, { payMethod: 'alipay' })
    ElMessage.success('支付成功！')
    loadOrders()
  } catch (error) {
    console.error('支付失败:', error)
  }
}

const handleConfirm = async (id) => {
  await ElMessageBox.confirm('确认已收到商品？', '确认收货', { type: 'warning' })
  try {
    await confirmOrder(id)
    ElMessage.success('确认收货成功！')
    loadOrders()
  } catch (error) {
    console.error('确认收货失败:', error)
  }
}

const handleCancel = async (id) => {
  await ElMessageBox.confirm('确认取消该订单？', '取消订单', { type: 'warning' })
  try {
    await cancelOrder(id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    console.error('取消订单失败:', error)
  }
}

const handleRefund = async (id) => {
  const { value: reason } = await ElMessageBox.prompt('请输入退款原因', '申请退款', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '请输入退款原因'
  }).catch(() => ({ value: null }))
  
  if (reason) {
    try {
      await applyRefund(id, { reason })
      ElMessage.success('退款申请已提交')
      loadOrders()
    } catch (error) {
      console.error('申请退款失败:', error)
    }
  }
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = { 
      page: page.value, 
      pageSize: 10, 
      status: activeTab.value !== 'all' ? parseInt(activeTab.value) : undefined 
    }
    const res = await getUserOrders(params)
    if (res.code === 200 && res.data) {
      if (res.data.content) {
        orders.value = res.data.content
        total.value = res.data.totalElements || 0
      } else if (Array.isArray(res.data)) {
        orders.value = res.data
        total.value = res.data.length
      }
    }
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => loadOrders())
</script>

<style scoped>
.orders-page { background:#fff; border-radius:12px; padding:24px; }
.page-title { font-size:22px; font-weight:700; color:#333; margin-bottom:16px; }
.order-card { border:1px solid #f0f0f0; border-radius:10px; margin-bottom:16px; overflow:hidden; }
.order-header { display:flex; align-items:center; gap:16px; background:#fafafa; padding:12px 16px; font-size:13px; }
.shop-name { font-weight:600; color:#333; }
.order-no { color:#666; }
.order-date { color:#999; }
.order-status { margin-left:auto; font-weight:600; }
.status-pending { color:#e6a23c; }
.status-shipped { color:#409eff; }
.status-delivering { color:#ff6b35; }
.status-done { color:#67c23a; }
.status-cancel { color:#909399; }
.status-refund { color:#f56c6c; }
.order-items { padding:16px; }
.order-item { display:flex; align-items:center; gap:12px; padding:8px 0; border-bottom:1px solid #f8f8f8; cursor:pointer; }
.order-item:last-child { border-bottom:none; }
.order-item img { width:70px; height:70px; object-fit:cover; border-radius:6px; flex-shrink:0; }
.item-info { flex:1; }
.item-name { font-size:14px; color:#333; margin-bottom:4px; }
.item-sku { font-size:12px; color:#999; }
.item-price { font-size:14px; color:#666; }
.order-footer { display:flex; align-items:center; justify-content:space-between; padding:12px 16px; background:#fafafa; border-top:1px solid #f0f0f0; }
.order-total { font-size:13px; color:#666; }
.total-price { font-size:18px; font-weight:700; color:#ff6b35; }
.shipping-text { font-size:12px; color:#999; }
.order-actions { display:flex; gap:8px; }
.pagination-wrap { display:flex; justify-content:center; margin-top:24px; }
</style>
