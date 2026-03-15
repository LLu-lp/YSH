<template>
  <div class="order-detail-page" v-loading="loading">
    <el-page-header @back="$router.back()" title="返回订单列表" content="订单详情" />

    <div class="detail-content" v-if="order">
      <div class="status-card">
        <div class="status-header">
          <div class="status-icon" :class="statusClass(order.status)">
            <el-icon size="32"><component :is="statusIcon(order.status)" /></el-icon>
          </div>
          <div>
            <p class="status-text">{{ statusText(order.status) }}</p>
            <p class="status-tip">{{ statusTip(order.status) }}</p>
          </div>
        </div>
        <el-steps :active="getStepActive(order.status)" align-center class="order-steps">
          <el-step title="提交订单" :description="formatDateTime(order.createTime)" />
          <el-step title="支付成功" :description="formatDateTime(order.payTime)" />
          <el-step title="商家发货" :description="formatDateTime(order.sendTime)" />
          <el-step title="确认收货" :description="formatDateTime(order.confirmTime)" />
        </el-steps>
      </div>

      <div class="section-card" v-if="order.expressNo">
        <h3 class="section-title"><el-icon><Van /></el-icon> 物流信息</h3>
        <p class="express-info">快递公司：{{ order.expressCompany }} | 快递单号：{{ order.expressNo }}</p>
      </div>

      <div class="section-card">
        <h3 class="section-title"><el-icon><Location /></el-icon> 收货地址</h3>
        <p><strong>{{ order.receiverName }}</strong> {{ order.receiverPhone }}</p>
        <p class="address-text">{{ order.receiverAddress }}</p>
      </div>

      <div class="section-card">
        <h3 class="section-title"><el-icon><ShoppingBag /></el-icon> 订单商品</h3>
        <div class="order-items">
          <div class="order-item" v-for="item in order.orderItems" :key="item.id">
            <img :src="item.productImage || '/placeholder.svg?height=80&width=80'" :alt="item.productName" />
            <div class="item-info">
              <p class="item-name">{{ item.productName }}</p>
              <p class="item-sku">{{ item.spec || '' }}</p>
            </div>
            <div class="item-price">¥{{ item.price }} × {{ item.quantity }}</div>
            <div class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          </div>
        </div>
        <div class="price-detail">
          <div class="price-row"><span>商品总价</span><span>¥{{ order.totalAmount }}</span></div>
          <div class="price-row"><span>运费</span><span>¥0.00</span></div>
          <div class="price-row total-row"><span>实付款</span><span class="total-price">¥{{ order.payAmount }}</span></div>
        </div>
      </div>

      <div class="section-card">
        <h3 class="section-title">订单信息</h3>
        <div class="info-grid">
          <div><span class="info-label">订单编号</span><span>{{ order.orderNo }}</span></div>
          <div><span class="info-label">下单时间</span><span>{{ formatDateTime(order.createTime) }}</span></div>
          <div v-if="order.payTime"><span class="info-label">支付时间</span><span>{{ formatDateTime(order.payTime) }}</span></div>
          <div v-if="order.payMethod"><span class="info-label">支付方式</span><span>{{ payMethodText(order.payMethod) }}</span></div>
          <div v-if="order.remark"><span class="info-label">订单备注</span><span>{{ order.remark }}</span></div>
        </div>
      </div>

      <div class="action-bar">
        <el-button v-if="order.status === 0" @click="handleCancel">取消订单</el-button>
        <el-button type="primary" v-if="order.status === 0" @click="handlePay">立即付款</el-button>
        <el-button v-if="order.status === 2" @click="handleRefund">申请售后</el-button>
        <el-button v-if="order.status === 2" type="primary" @click="handleConfirm">确认收货</el-button>
        <el-button v-if="order.status === 3" type="primary" @click="handleReview">评价商品</el-button>
      </div>
    </div>

    <!-- 评价对话框 -->
    <el-dialog v-model="showReviewDialog" title="评价商品" width="600px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="选择商品">
          <el-select v-model="reviewForm.productId" placeholder="请选择要评价的商品" style="width: 100%">
            <el-option
              v-for="item in order?.orderItems"
              :key="item.productId"
              :label="item.productName"
              :value="item.productId"
              :disabled="item.reviewed === 1"
            >
              <div style="display: flex; align-items: center; gap: 8px;">
                <img :src="item.productImage" style="width: 32px; height: 32px; border-radius: 4px;" />
                <span>{{ item.productName }}</span>
                <el-tag v-if="item.reviewed === 1" size="small" type="success">已评价</el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请分享您的使用体验"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="晒图">
          <el-input v-model="reviewForm.images" placeholder="图片URL，多个用逗号分隔（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="reviewLoading">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Van, Location, ShoppingBag, WalletFilled, Box, CircleCheck, Warning } from '@element-plus/icons-vue'
import { getOrderDetail, payOrder, confirmOrder, cancelOrder, applyRefund, submitReview as submitProductReview } from '@/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref(null)
const showReviewDialog = ref(false)
const reviewLoading = ref(false)
const reviewForm = ref({
  productId: null,
  rating: 5,
  content: '',
  images: ''
})

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

const statusText = (s) => ({ 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消', 5: '售后/退款' }[s] || '未知')
const statusTip = (s) => ({ 0: '请尽快完成支付', 1: '商家正在备货，即将发货', 2: '包裹正在路上，请耐心等待', 3: '交易已完成，感谢您的购买', 4: '订单已取消', 5: '售后处理中' }[s] || '')
const statusClass = (s) => ({ 0: 'pending', 1: 'shipped', 2: 'delivering', 3: 'done', 4: 'cancel', 5: 'refund' }[s])
const statusIcon = (s) => ({ 0: WalletFilled, 1: Box, 2: Van, 3: CircleCheck, 4: Warning, 5: Warning }[s])
const payMethodText = (m) => ({ alipay: '支付宝', wechat: '微信支付' }[m] || m || '-')

const getStepActive = (status) => {
  if (status === 0) return 0
  if (status === 1) return 1
  if (status === 2) return 2
  if (status >= 3) return 3
  return 0
}

const loadOrder = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    if (res.code === 200 && res.data) {
      order.value = res.data
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePay = async () => {
  await ElMessageBox.confirm('确认支付该订单？（模拟支付）', '支付确认', { type: 'warning' })
  try {
    await payOrder(order.value.id, { payMethod: 'alipay' })
    ElMessage.success('支付成功！')
    loadOrder()
  } catch (error) {
    console.error('支付失败:', error)
  }
}

const handleConfirm = async () => {
  await ElMessageBox.confirm('确认已收到商品？', '确认收货', { type: 'warning' })
  try {
    await confirmOrder(order.value.id)
    ElMessage.success('确认收货成功！')
    loadOrder()
  } catch (error) {
    console.error('确认收货失败:', error)
  }
}

const handleCancel = async () => {
  await ElMessageBox.confirm('确认取消订单？', '取消订单', { type: 'warning' })
  try {
    await cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    router.push('/user/orders')
  } catch (error) {
    console.error('取消订单失败:', error)
  }
}

const handleRefund = async () => {
  const { value: reason } = await ElMessageBox.prompt('请输入退款原因', '申请售后', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '请输入退款原因'
  }).catch(() => ({ value: null }))
  
  if (reason) {
    try {
      await applyRefund(order.value.id, { reason })
      ElMessage.success('售后申请已提交')
      loadOrder()
    } catch (error) {
      console.error('申请售后失败:', error)
    }
  }
}

const handleReview = () => {
  reviewForm.value = {
    productId: null,
    rating: 5,
    content: '',
    images: ''
  }
  showReviewDialog.value = true
}

const submitReview = async () => {
  if (!reviewForm.value.productId) {
    ElMessage.warning('请选择要评价的商品')
    return
  }
  if (!reviewForm.value.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }

  reviewLoading.value = true
  try {
    await submitProductReview(order.value.id, {
      productId: reviewForm.value.productId,
      rating: reviewForm.value.rating,
      content: reviewForm.value.content,
      images: reviewForm.value.images
    })
    ElMessage.success('评价提交成功！')
    showReviewDialog.value = false
    loadOrder()
  } catch (error) {
    console.error('提交评价失败:', error)
  } finally {
    reviewLoading.value = false
  }
}

onMounted(() => loadOrder())
</script>

<style scoped>
.order-detail-page { background: #fff; border-radius: 12px; padding: 24px; }
.detail-content { margin-top: 24px; display: flex; flex-direction: column; gap: 16px; }
.status-card { background: linear-gradient(135deg, #fff8f5, #fff3ee); border-radius: 12px; padding: 24px; }
.status-header { display: flex; align-items: center; gap: 20px; margin-bottom: 24px; }
.status-icon { width: 64px; height: 64px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.pending { background: #fef3e2; color: #e6a23c; }
.shipped, .delivering { background: #ecf5ff; color: #409eff; }
.done { background: #f0f9eb; color: #67c23a; }
.cancel, .refund { background: #fef0f0; color: #f56c6c; }
.status-text { font-size: 20px; font-weight: 700; color: #333; }
.status-tip { font-size: 14px; color: #999; margin-top: 4px; }
.order-steps { margin-top: 8px; }
.section-card { background: #fff; border: 1px solid #f0f0f0; border-radius: 10px; padding: 20px; }
.section-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 700; color: #333; margin-bottom: 16px; }
.express-info { font-size: 14px; color: #666; }
.address-text { font-size: 14px; color: #999; margin-top: 4px; }
.order-items { display: flex; flex-direction: column; gap: 12px; margin-bottom: 16px; }
.order-item { display: flex; align-items: center; gap: 16px; padding: 12px 0; border-bottom: 1px solid #f8f8f8; }
.order-item img { width: 80px; height: 80px; object-fit: cover; border-radius: 8px; flex-shrink: 0; background: #f5f5f4; }
.item-info { flex: 1; }
.item-name { font-size: 14px; color: #333; margin-bottom: 4px; }
.item-sku { font-size: 12px; color: #999; }
.item-price { font-size: 14px; color: #666; min-width: 100px; text-align: center; }
.item-subtotal { font-size: 15px; font-weight: 600; color: #333; min-width: 80px; text-align: right; }
.price-detail { border-top: 1px solid #f0f0f0; padding-top: 16px; }
.price-row { display: flex; justify-content: space-between; font-size: 14px; color: #666; padding: 6px 0; }
.total-row { font-size: 16px; font-weight: 700; color: #333; padding-top: 12px; border-top: 1px solid #f0f0f0; margin-top: 6px; }
.total-price { color: #ff6b35; font-size: 22px; }
.info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.info-grid > div { display: flex; gap: 12px; font-size: 14px; }
.info-label { color: #999; min-width: 70px; }
.action-bar { display: flex; justify-content: flex-end; gap: 12px; }
</style>
