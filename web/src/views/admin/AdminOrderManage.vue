<template>
  <div class="order-manage">
    <div class="page-header">
      <h2>订单管理</h2>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <input v-model="searchKeyword" placeholder="搜索订单号/用户名" class="search-input" />
      <select v-model="statusFilter" class="filter-select">
        <option value="">全部状态</option>
        <option value="0">待付款</option>
        <option value="1">待发货</option>
        <option value="2">已发货</option>
        <option value="3">已完成</option>
        <option value="4">已取消</option>
        <option value="5">退款中</option>
      </select>
      <button @click="handleSearch" class="btn-primary">搜索</button>
      <button @click="handleReset" class="btn-outline">重置</button>
    </div>

    <!-- 订单表格 -->
    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>用户</th>
            <th>商家</th>
            <th>金额</th>
            <th>下单时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orderList" :key="order.id">
            <td class="order-no">{{ order.orderNo }}</td>
            <td>{{ order.username }}</td>
            <td>{{ order.shopName }}</td>
            <td class="price">¥{{ order.totalAmount }}</td>
            <td>{{ order.createTime }}</td>
            <td>
              <span :class="['status-badge', 'status-' + order.status]">
                {{ statusMap[order.status] }}
              </span>
            </td>
            <td>
              <button @click="viewOrder(order)" class="btn-link">详情</button>
              <button v-if="order.status === 5" @click="handleRefund(order)" class="btn-link warning">
                处理退款
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="orderList.length === 0" class="empty">暂无订单数据</div>
      <div class="pagination">
        <button :disabled="page <= 1" @click="page--; loadOrders()">上一页</button>
        <span>第 {{ page }} / {{ totalPages }} 页，共 {{ total }} 条</span>
        <button :disabled="page >= totalPages" @click="page++; loadOrders()">下一页</button>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <div v-if="showDetail" class="modal-overlay" @click.self="showDetail = false">
      <div class="modal">
        <div class="modal-header">
          <h3>订单详情</h3>
          <button @click="showDetail = false" class="close-btn">×</button>
        </div>
        <div class="modal-body" v-if="currentOrder">
          <div class="detail-item"><label>订单号：</label><span>{{ currentOrder.orderNo }}</span></div>
          <div class="detail-item"><label>买家：</label><span>{{ currentOrder.username }}</span></div>
          <div class="detail-item"><label>商家：</label><span>{{ currentOrder.shopName }}</span></div>
          <div class="detail-item"><label>收货地址：</label><span>{{ currentOrder.receiverAddress }}</span></div>
          <div class="detail-item"><label>订单金额：</label><span class="price">¥{{ currentOrder.totalAmount }}</span></div>
          <div class="detail-item"><label>下单时间：</label><span>{{ currentOrder.createTime }}</span></div>
          <div class="detail-item"><label>支付时间：</label><span>{{ currentOrder.payTime || '-' }}</span></div>
          <div class="detail-item"><label>状态：</label>
            <span :class="['status-badge', 'status-' + currentOrder.status]">{{ statusMap[currentOrder.status] }}</span>
          </div>
          <div class="product-list" v-if="currentOrder.orderItems?.length">
            <p class="section-title">商品清单</p>
            <div v-for="p in currentOrder.orderItems" :key="p.id" class="product-row">
              <img :src="p.productImage || '/placeholder.svg?height=40&width=40'" class="product-img" alt="商品图" />
              <span class="product-name">{{ p.productName }}</span>
              <span class="product-price">¥{{ p.price }} × {{ p.quantity }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 退款处理弹窗 -->
    <div v-if="showRefund" class="modal-overlay" @click.self="showRefund = false">
      <div class="modal">
        <div class="modal-header">
          <h3>处理退款申请</h3>
          <button @click="showRefund = false" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <p style="color:#374151;margin-bottom:16px;">
            订单号：<strong>{{ currentOrder?.orderNo }}</strong>，退款金额：<strong class="price">¥{{ currentOrder?.totalAmount }}</strong>
          </p>
          <div class="form-item">
            <label>处理意见</label>
            <textarea v-model="refundRemark" rows="3" placeholder="请输入处理意见..." class="textarea"></textarea>
          </div>
          <div class="modal-footer">
            <button @click="approveRefund" class="btn-success">同意退款</button>
            <button @click="rejectRefund" class="btn-danger">拒绝退款</button>
            <button @click="showRefund = false" class="btn-outline">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGetOrders, adminHandleRefund } from '@/api'

const searchKeyword = ref('')
const statusFilter = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)
const showDetail = ref(false)
const showRefund = ref(false)
const currentOrder = ref(null)
const refundRemark = ref('')

const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)

const statusMap = { 0: '待付款', 1: '待发货', 2: '已发货', 3: '已完成', 4: '已取消', 5: '退款中' }

const orderList = ref([])

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      keyword: searchKeyword.value || undefined,
      status: statusFilter.value || undefined,
      page: page.value,
      pageSize: pageSize
    }
    const res = await adminGetOrders(params)
    if (res.code === 200 && res.data) {
      if (res.data.content) {
        orderList.value = res.data.content.map(o => ({
          ...o,
          createTime: formatDateTime(o.createTime),
          payTime: formatDateTime(o.payTime)
        }))
        total.value = res.data.totalElements || 0
      } else if (Array.isArray(res.data)) {
        orderList.value = res.data.map(o => ({
          ...o,
          createTime: formatDateTime(o.createTime),
          payTime: formatDateTime(o.payTime)
        }))
        total.value = res.data.length
      }
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => loadOrders())

function handleSearch() { page.value = 1; loadOrders() }
function handleReset() { searchKeyword.value = ''; statusFilter.value = ''; page.value = 1; loadOrders() }

function viewOrder(order) {
  currentOrder.value = order
  showDetail.value = true
}

function handleRefund(order) {
  currentOrder.value = order
  refundRemark.value = ''
  showRefund.value = true
}

async function approveRefund() {
  if (!refundRemark.value.trim()) {
    ElMessage.warning('请填写处理意见')
    return
  }
  try {
    await adminHandleRefund(currentOrder.value.id, { action: 'approve', remark: refundRemark.value })
    currentOrder.value.status = 4
    showRefund.value = false
    ElMessage.success('退款已同意')
    loadOrders()
  } catch (error) {
    console.error('处理退款失败:', error)
  }
}

async function rejectRefund() {
  if (!refundRemark.value.trim()) {
    ElMessage.warning('请填写处理意见')
    return
  }
  try {
    await adminHandleRefund(currentOrder.value.id, { action: 'reject', remark: refundRemark.value })
    currentOrder.value.status = 3
    showRefund.value = false
    ElMessage.success('退款已拒绝')
    loadOrders()
  } catch (error) {
    console.error('处理退款失败:', error)
  }
}
</script>

<style scoped>
.order-manage { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #1f2937; }
.search-bar { display: flex; gap: 10px; margin-bottom: 20px; align-items: center; flex-wrap: wrap; }
.search-input { padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; width: 220px; font-size: 14px; }
.filter-select { padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; }
.btn-primary { padding: 8px 18px; background: #ff6b35; color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; }
.btn-outline { padding: 8px 18px; background: #fff; color: #374151; border: 1px solid #d1d5db; border-radius: 6px; cursor: pointer; font-size: 14px; }
.table-card { background: #fff; border-radius: 10px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.07); }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th { background: #f9fafb; padding: 12px 10px; text-align: left; color: #6b7280; font-weight: 500; border-bottom: 1px solid #e5e7eb; }
.data-table td { padding: 12px 10px; border-bottom: 1px solid #f3f4f6; color: #374151; }
.order-no { font-family: monospace; font-size: 13px; }
.price { color: #ef4444; font-weight: 600; }
.status-badge { padding: 2px 10px; border-radius: 20px; font-size: 12px; }
.status-0 { background: #fef9c3; color: #92400e; }
.status-1 { background: #dbeafe; color: #1d4ed8; }
.status-2 { background: #ede9fe; color: #6d28d9; }
.status-3 { background: #d1fae5; color: #065f46; }
.status-4 { background: #f3f4f6; color: #6b7280; }
.status-5 { background: #fee2e2; color: #991b1b; }
.btn-link { background: none; border: none; cursor: pointer; font-size: 13px; color: #ff6b35; margin-right: 8px; padding: 0; }
.btn-link.warning { color: #d97706; }
.empty { text-align: center; padding: 40px; color: #9ca3af; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 12px; margin-top: 16px; font-size: 14px; color: #6b7280; }
.pagination button { padding: 6px 14px; border: 1px solid #d1d5db; border-radius: 6px; background: #fff; cursor: pointer; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: #fff; border-radius: 12px; width: 500px; max-height: 80vh; overflow-y: auto; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #e5e7eb; position: sticky; top: 0; background: #fff; }
.modal-header h3 { font-size: 16px; font-weight: 600; color: #1f2937; }
.close-btn { background: none; border: none; font-size: 22px; cursor: pointer; color: #9ca3af; }
.modal-body { padding: 20px; }
.detail-item { display: flex; align-items: flex-start; padding: 8px 0; font-size: 14px; border-bottom: 1px solid #f3f4f6; }
.detail-item label { width: 90px; color: #6b7280; flex-shrink: 0; }
.section-title { font-weight: 600; color: #1f2937; margin: 12px 0 8px; }
.product-row { display: flex; align-items: center; gap: 10px; padding: 8px 0; border-bottom: 1px solid #f3f4f6; }
.product-img { width: 40px; height: 40px; object-fit: cover; border-radius: 6px; }
.product-name { flex: 1; font-size: 14px; color: #374151; }
.product-price { color: #ef4444; font-size: 13px; }
.form-item { margin-bottom: 16px; }
.form-item label { display: block; font-size: 14px; color: #374151; margin-bottom: 6px; font-weight: 500; }
.textarea { width: 100%; padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; resize: vertical; box-sizing: border-box; }
.modal-footer { display: flex; gap: 10px; justify-content: flex-end; margin-top: 16px; }
.btn-success { padding: 8px 18px; background: #16a34a; color: #fff; border: none; border-radius: 6px; cursor: pointer; }
.btn-danger { padding: 8px 18px; background: #dc2626; color: #fff; border: none; border-radius: 6px; cursor: pointer; }
</style>
