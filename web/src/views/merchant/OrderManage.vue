<template>
  <div class="orders-page">
    <div class="page-header">
      <h2>订单管理</h2>
    </div>

    <div class="status-tabs">
      <button
        v-for="tab in tabs" :key="tab.value"
        class="tab-btn" :class="{ active: activeTab === tab.value }"
        @click="switchTab(tab.value)">
        {{ tab.label }}
      </button>
    </div>

    <div class="filter-bar">
      <input v-model="filters.keyword" type="text" placeholder="搜索订单号 / 买家名称..." @keyup.enter="loadOrders" />
      <button class="btn-search" @click="loadOrders">搜索</button>
    </div>

    <div class="order-list" v-loading="loading">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-meta">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ formatDateTime(order.createTime) }}</span>
          </div>
          <span class="status-tag" :class="statusMap[order.status]?.cls">{{ statusMap[order.status]?.text }}</span>
        </div>
        <div class="order-body">
          <div class="order-product">
            <img :src="getFirstItemImage(order.orderItems) || '/placeholder.svg?height=60&width=60'" class="product-thumb" alt="商品图片" />
            <div class="product-info">
              <p class="product-name">{{ getFirstItemName(order.orderItems) }}</p>
              <p class="product-spec">共 {{ (order.orderItems || []).length }} 件商品</p>
            </div>
            <div class="product-price">¥{{ order.payAmount || order.totalAmount }}</div>
          </div>
          <div class="order-summary">
            <div class="summary-item">
              <span>买家</span>
              <span>{{ order.receiverName }}</span>
            </div>
            <div class="summary-item">
              <span>收货地址</span>
              <span>{{ order.receiverAddress }}</span>
            </div>
            <div class="summary-item">
              <span>实付金额</span>
              <strong class="amount">¥{{ order.payAmount || order.totalAmount }}</strong>
            </div>
          </div>
        </div>
        <div class="order-footer">
          <div class="tracking-info" v-if="order.expressCompany && order.expressNo">
            {{ order.expressCompany }} {{ order.expressNo }}
          </div>
          <div class="order-actions">
            <button v-if="order.status === 1" class="btn-action primary" @click="openShipModal(order)">确认发货</button>
            <template v-if="order.status === 5">
              <button class="btn-action warning" @click="handleRefundApprove(order)">同意退款</button>
              <button class="btn-action danger" @click="handleRefundReject(order)">拒绝退款</button>
            </template>
          </div>
        </div>
      </div>
      <div v-if="orders.length === 0 && !loading" class="empty-state">
        <p>暂无订单数据</p>
      </div>
    </div>

    <div class="pagination">
      <button @click="page > 1 && (page--, loadOrders())" :disabled="page === 1">上一页</button>
      <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
      <button @click="page < totalPages && (page++, loadOrders())" :disabled="page === totalPages">下一页</button>
    </div>

    <div v-if="showShipModal" class="modal-mask" @click.self="showShipModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>确认发货</h3>
          <button @click="showShipModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>快递公司 <span class="required">*</span></label>
            <select v-model="shipForm.expressCompany">
              <option v-for="exp in expressList" :key="exp" :value="exp">{{ exp }}</option>
            </select>
          </div>
          <div class="form-item">
            <label>快递单号 <span class="required">*</span></label>
            <input v-model="shipForm.trackingNo" type="text" placeholder="请输入快递单号" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showShipModal = false">取消</button>
          <button class="btn-confirm" @click="handleShip" :disabled="shipping">{{ shipping ? '发货中...' : '确认发货' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMerchantOrders, shipOrder, handleRefund } from '@/api'

const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)
const activeTab = ref('')
const showShipModal = ref(false)
const currentOrder = ref(null)
const shipping = ref(false)

const tabs = ref([
  { label: '全部', value: '' },
  { label: '待付款', value: '0' },
  { label: '待发货', value: '1' },
  { label: '已发货', value: '2' },
  { label: '已完成', value: '3' },
  { label: '已退款', value: '4' },
  { label: '退款申请', value: '5' }
])

const statusMap = {
  0: { text: '待付款', cls: 'pending' },
  1: { text: '待发货', cls: 'warning' },
  2: { text: '已发货', cls: 'info' },
  3: { text: '已完成', cls: 'success' },
  4: { text: '已退款', cls: 'off' },
  5: { text: '退款申请', cls: 'danger' }
}

const filters = ref({ keyword: '' })

const expressList = ['顺丰快递', '圆通速递', '申通快递', '中通快递', '韵达快递', '京东快递', 'EMS']

const shipForm = ref({ trackingNo: '', expressCompany: '顺丰快递' })

const orders = ref([])

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

const getFirstItemImage = (items) => {
  if (!items || items.length === 0) return null
  return items[0].productImage
}

const getFirstItemName = (items) => {
  if (!items || items.length === 0) return '未知商品'
  return items[0].productName
}

const switchTab = (val) => {
  activeTab.value = val
  page.value = 1
  loadOrders()
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize,
      status: activeTab.value !== '' ? parseInt(activeTab.value) : undefined,
      keyword: filters.value.keyword || undefined
    }
    const res = await getMerchantOrders(params)
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

const openShipModal = (order) => {
  currentOrder.value = order
  shipForm.value = { trackingNo: '', expressCompany: '顺丰快递' }
  showShipModal.value = true
}

const handleShip = async () => {
  if (!shipForm.value.trackingNo) {
    ElMessage.warning('请输入快递单号')
    return
  }
  shipping.value = true
  try {
    await shipOrder(currentOrder.value.id, {
      expressCompany: shipForm.value.expressCompany,
      expressNo: shipForm.value.trackingNo
    })
    currentOrder.value.status = 2
    currentOrder.value.expressCompany = shipForm.value.expressCompany
    currentOrder.value.expressNo = shipForm.value.trackingNo
    showShipModal.value = false
    ElMessage.success('发货成功')
  } catch (error) {
    console.error('发货失败:', error)
  } finally {
    shipping.value = false
  }
}

const handleRefundApprove = async (order) => {
  if (!confirm('确认同意退款申请？')) return
  try {
    await handleRefund(order.id, { action: 'approve', remark: '' })
    order.status = 4
    ElMessage.success('已同意退款')
  } catch (error) {
    console.error('处理退款失败:', error)
  }
}

const handleRefundReject = async (order) => {
  const reason = prompt('请输入拒绝原因：')
  if (!reason) return
  try {
    await handleRefund(order.id, { action: 'reject', remark: reason })
    ElMessage.success('已拒绝退款')
    loadOrders()
  } catch (error) {
    console.error('处理退款失败:', error)
  }
}

onMounted(() => loadOrders())
</script>

<style scoped>
.orders-page { padding: 28px 32px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0; }
.status-tabs { display: flex; gap: 4px; margin-bottom: 16px; border-bottom: 1px solid #e7e5e4; }
.tab-btn { padding: 10px 16px; background: none; border: none; font-size: 13px; color: #78716c; cursor: pointer; border-bottom: 2px solid transparent; margin-bottom: -1px; }
.tab-btn.active { color: #f97316; border-bottom-color: #f97316; font-weight: 600; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.filter-bar input { padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; min-width: 220px; }
.btn-search { padding: 9px 18px; background: #f97316; color: white; border: none; border-radius: 8px; font-size: 13px; cursor: pointer; }
.order-list { display: flex; flex-direction: column; gap: 12px; }
.order-card { background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.order-header { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; background: #fafaf9; border-bottom: 1px solid #f5f5f4; }
.order-meta { display: flex; gap: 16px; align-items: center; }
.order-no { font-size: 13px; font-weight: 600; color: #44403c; font-family: monospace; }
.order-time { font-size: 12px; color: #a8a29e; }
.status-tag { padding: 3px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.status-tag.pending { background: #fefce8; color: #ca8a04; }
.status-tag.warning { background: #fff7ed; color: #f97316; }
.status-tag.info { background: #eff6ff; color: #3b82f6; }
.status-tag.success { background: #f0fdf4; color: #10b981; }
.status-tag.danger { background: #fef2f2; color: #ef4444; }
.status-tag.off { background: #f5f5f4; color: #a8a29e; }
.order-body { display: flex; padding: 16px; gap: 20px; }
.order-product { display: flex; gap: 12px; align-items: flex-start; flex: 1; }
.product-thumb { width: 60px; height: 60px; border-radius: 8px; object-fit: cover; background: #f5f5f4; }
.product-name { font-size: 14px; font-weight: 500; color: #1c1917; margin: 0 0 4px; }
.product-spec { font-size: 12px; color: #a8a29e; margin: 0; }
.product-price { font-size: 15px; font-weight: 600; color: #f97316; }
.order-summary { width: 280px; flex-shrink: 0; border-left: 1px solid #f5f5f4; padding-left: 20px; }
.summary-item { display: flex; justify-content: space-between; font-size: 13px; color: #78716c; margin-bottom: 8px; }
.amount { font-size: 15px; font-weight: 700; color: #f97316; }
.order-footer { display: flex; justify-content: space-between; align-items: center; padding: 10px 16px; border-top: 1px solid #f5f5f4; }
.tracking-info { font-size: 12px; color: #78716c; }
.order-actions { display: flex; gap: 8px; margin-left: auto; }
.btn-action { padding: 6px 14px; border-radius: 7px; font-size: 12px; cursor: pointer; border: 1.5px solid; font-weight: 500; }
.btn-action.primary { background: #f97316; color: white; border-color: #f97316; }
.btn-action.warning { background: #fff7ed; color: #f97316; border-color: #fed7aa; }
.btn-action.danger { background: #fef2f2; color: #ef4444; border-color: #fecaca; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 12px; margin-top: 16px; font-size: 13px; color: #78716c; }
.pagination button { padding: 6px 14px; border: 1.5px solid #e7e5e4; border-radius: 6px; background: white; font-size: 13px; cursor: pointer; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: white; border-radius: 14px; width: 460px; overflow: hidden; }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 18px 22px; border-bottom: 1px solid #f5f5f4; }
.modal-header h3 { font-size: 16px; font-weight: 600; color: #1c1917; margin: 0; }
.modal-close { background: none; border: none; font-size: 22px; color: #a8a29e; cursor: pointer; }
.modal-body { padding: 20px 22px; display: flex; flex-direction: column; gap: 14px; }
.form-item { display: flex; flex-direction: column; gap: 6px; }
.form-item label { font-size: 13px; color: #78716c; font-weight: 500; }
.required { color: #ef4444; }
.form-item input, .form-item select { padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; color: #1c1917; outline: none; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 14px 22px; border-top: 1px solid #f5f5f4; }
.btn-cancel { padding: 9px 20px; background: white; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; cursor: pointer; }
.btn-confirm { padding: 9px 20px; background: #f97316; color: white; border: none; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; }
.btn-confirm:disabled { opacity: 0.6; cursor: not-allowed; }
.empty-state { text-align: center; padding: 40px; color: #a8a29e; }
</style>
