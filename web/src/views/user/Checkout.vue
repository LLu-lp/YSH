<template>
  <div class="checkout-page">
    <div class="page-header">
      <h2>确认订单</h2>
    </div>

    <div class="checkout-content">
      <!-- 收货地址 -->
      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <span>收货地址</span>
            <el-button link type="primary" @click="showAddressDialog = true">管理地址</el-button>
          </div>
        </template>
        <div v-if="selectedAddress" class="address-item selected">
          <div class="address-info">
            <span class="receiver">{{ selectedAddress.receiver }}</span>
            <span class="phone">{{ selectedAddress.phone }}</span>
            <el-tag v-if="selectedAddress.isDefault" size="small" type="danger">默认</el-tag>
          </div>
          <p class="address-detail">{{ selectedAddress.province }} {{ selectedAddress.city }} {{ selectedAddress.district }} {{ selectedAddress.detail }}</p>
        </div>
        <el-empty v-else description="请选择收货地址" :image-size="80">
          <el-button type="primary" @click="showAddressDialog = true">添加地址</el-button>
        </el-empty>
      </el-card>

      <!-- 商品清单 -->
      <el-card class="section-card">
        <template #header>商品清单</template>
        <div class="product-list">
          <div class="product-item" v-for="item in orderItems" :key="item.id">
            <img :src="item.productImage || item.mainImage || '/placeholder.svg?height=80&width=80'" :alt="item.productName || item.name" />
            <div class="item-info">
              <p class="item-name">{{ item.productName || item.name }}</p>
              <p class="item-spec">{{ item.spec || '' }}</p>
            </div>
            <div class="item-price">¥{{ item.price }}</div>
            <div class="item-qty">× {{ item.quantity }}</div>
            <div class="item-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          </div>
        </div>
      </el-card>

      <!-- 支付方式 -->
      <el-card class="section-card">
        <template #header>支付方式</template>
        <el-radio-group v-model="payMethod">
          <el-radio value="alipay">支付宝</el-radio>
          <el-radio value="wechat">微信支付</el-radio>
          <el-radio value="balance">余额支付</el-radio>
        </el-radio-group>
      </el-card>

      <!-- 优惠券 -->
      <el-card class="section-card">
        <template #header>优惠券</template>
        <div class="coupon-section">
          <el-select v-model="selectedCouponId" placeholder="选择优惠券" clearable style="width: 100%">
            <el-option v-for="coupon in availableCoupons" :key="coupon.id" :label="`${coupon.name} (¥${coupon.value})`" :value="coupon.id" />
          </el-select>
          <div v-if="selectedCoupon" class="coupon-info">
            <p>已选择：{{ selectedCoupon.name }}</p>
            <p>优惠金额：¥{{ selectedCoupon.value }}</p>
          </div>
        </div>
      </el-card>

      <!-- 订单备注 -->
      <el-card class="section-card">
        <template #header>订单备注</template>
        <el-input v-model="remark" type="textarea" :rows="3" placeholder="选填，可以告诉商家您的特殊需求" maxlength="200" show-word-limit />
      </el-card>
    </div>

    <!-- 结算栏 -->
    <div class="checkout-bar">
      <div class="price-summary">
        <div class="summary-row">
          <span>商品总价：</span>
          <span>¥{{ totalAmount }}</span>
        </div>
        <div class="summary-row" v-if="selectedCoupon">
          <span>优惠券：</span>
          <span>-¥{{ selectedCoupon.value }}</span>
        </div>
        <div class="summary-row">
          <span>运费：</span>
          <span>¥0.00</span>
        </div>
        <div class="summary-row total">
          <span>应付总额：</span>
          <span class="total-price">¥{{ finalAmount }}</span>
        </div>
      </div>
      <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">
        提交订单
      </el-button>
    </div>

    <!-- 地址选择弹窗 -->
    <el-dialog v-model="showAddressDialog" title="选择收货地址" width="600px">
      <div class="address-list">
        <div
          v-for="addr in addressList"
          :key="addr.id"
          class="address-card"
          :class="{ selected: selectedAddress?.id === addr.id }"
          @click="selectAddress(addr)"
        >
          <div class="address-info">
            <span class="receiver">{{ addr.receiver }}</span>
            <span class="phone">{{ addr.phone }}</span>
            <el-tag v-if="addr.isDefault" size="small" type="danger">默认</el-tag>
          </div>
          <p class="address-detail">{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detail }}</p>
        </div>
        <el-empty v-if="addressList.length === 0" description="暂无收货地址">
          <el-button type="primary" @click="$router.push('/user/address')">去添加</el-button>
        </el-empty>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAddresses, getCartList, createOrder, getUserCoupons } from '@/api'

const route = useRoute()
const router = useRouter()
const submitting = ref(false)
const showAddressDialog = ref(false)
const selectedAddress = ref(null)
const addressList = ref([])
const orderItems = ref([])
const payMethod = ref('alipay')
const remark = ref('')
const selectedCouponId = ref(null)
const availableCoupons = ref([])

const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + (item.price || 0) * (item.quantity || 0), 0).toFixed(2)
})

const selectedCoupon = computed(() => {
  return availableCoupons.value.find(c => c.id === selectedCouponId.value)
})

const finalAmount = computed(() => {
  const total = parseFloat(totalAmount.value)
  const discount = selectedCoupon.value ? selectedCoupon.value.value : 0
  return Math.max(0, total - discount).toFixed(2)
})

const loadAddresses = async () => {
  try {
    const res = await getAddresses()
    if (res.code === 200 && res.data) {
      addressList.value = Array.isArray(res.data) ? res.data : res.data.content || []
      const defaultAddr = addressList.value.find(a => a.isDefault === 1)
      if (defaultAddr) {
        selectedAddress.value = defaultAddr
      } else if (addressList.value.length > 0) {
        selectedAddress.value = addressList.value[0]
      }
    }
  } catch (error) {
    console.error('加载地址失败:', error)
  }
}

const loadCoupons = async () => {
  try {
    const res = await getUserCoupons()
    if (res.code === 200 && res.data) {
      const now = new Date()
      availableCoupons.value = res.data.filter(c => c.status === 1 && new Date(c.endTime) > now)
    }
  } catch (error) {
    console.error('加载优惠券失败:', error)
  }
}

const loadOrderItems = async () => {
  const { action, productId, quantity, items } = route.query
  
  if (action === 'buy' && productId) {
    // 立即购买 - 需要先获取商品详情
    try {
      const { getProductDetail } = await import('@/api')
      const res = await getProductDetail(productId)
      if (res.code === 200 && res.data) {
        orderItems.value = [{
          productId: res.data.id,
          productName: res.data.name,
          productImage: res.data.mainImage,
          price: res.data.price,
          quantity: parseInt(quantity) || 1,
          spec: ''
        }]
      }
    } catch (error) {
      console.error('加载商品失败:', error)
      ElMessage.error('加载商品信息失败')
    }
  } else if (action === 'cart' && items) {
    // 购物车结算
    try {
      const itemIds = JSON.parse(items)
      const res = await getCartList()
      if (res.code === 200 && res.data) {
        const allItems = Array.isArray(res.data) ? res.data : res.data.content || []
        orderItems.value = allItems.filter(i => itemIds.includes(i.id))
      }
    } catch (error) {
      console.error('加载购物车失败:', error)
      ElMessage.error('加载购物车信息失败')
    }
  }
}

const selectAddress = (addr) => {
  selectedAddress.value = addr
  showAddressDialog.value = false
}

const handleSubmit = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (orderItems.value.length === 0) {
    ElMessage.warning('订单商品为空')
    return
  }

  submitting.value = true
  try {
    const orderData = {
      addressId: selectedAddress.value.id,
      items: orderItems.value.map(i => ({
        productId: i.productId || i.id,
        quantity: i.quantity,
        price: i.price
      })),
      payMethod: payMethod.value,
      remark: remark.value,
      couponId: selectedCouponId.value || null
    }
    const res = await createOrder(orderData)
    if (res.code === 200) {
      ElMessage.success('订单创建成功')
      router.push('/user/orders')
    } else {
      ElMessage.error(res.message || '订单创建失败')
    }
  } catch (error) {
    console.error('提交订单失败:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadAddresses()
  loadCoupons()
  loadOrderItems()
})
</script>

<style scoped>
.checkout-page { max-width: 1000px; margin: 0 auto; }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 24px; font-weight: 700; color: #333; }
.checkout-content { display: flex; flex-direction: column; gap: 16px; margin-bottom: 80px; }
.section-card { border-radius: 10px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.address-item { padding: 16px; border: 2px solid #f0f0f0; border-radius: 8px; cursor: pointer; transition: all 0.2s; }
.address-item.selected { border-color: #ff6b35; background: #fff8f5; }
.address-info { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.receiver { font-size: 15px; font-weight: 600; color: #333; }
.phone { font-size: 14px; color: #666; }
.address-detail { font-size: 14px; color: #666; margin: 0; }
.product-list { display: flex; flex-direction: column; gap: 12px; }
.product-item { display: flex; align-items: center; gap: 16px; padding: 12px; border: 1px solid #f5f5f5; border-radius: 8px; }
.product-item img { width: 80px; height: 80px; object-fit: cover; border-radius: 6px; }
.item-info { flex: 1; }
.item-name { font-size: 14px; color: #333; margin-bottom: 4px; }
.item-spec { font-size: 12px; color: #999; }
.item-price, .item-qty { font-size: 14px; color: #666; width: 80px; text-align: center; }
.item-total { font-size: 16px; font-weight: 600; color: #ff6b35; width: 100px; text-align: right; }
.checkout-bar { position: fixed; bottom: 0; left: 0; right: 0; background: #fff; border-top: 1px solid #f0f0f0; padding: 16px 24px; display: flex; align-items: center; justify-content: flex-end; gap: 24px; box-shadow: 0 -2px 10px rgba(0,0,0,0.06); z-index: 100; }
.price-summary { display: flex; flex-direction: column; gap: 8px; }
.summary-row { display: flex; justify-content: space-between; gap: 40px; font-size: 14px; color: #666; }
.summary-row.total { font-size: 16px; color: #333; font-weight: 600; }
.total-price { font-size: 24px; color: #ff6b35; font-weight: 700; }
.address-list { display: flex; flex-direction: column; gap: 12px; max-height: 400px; overflow-y: auto; }
.address-card { padding: 16px; border: 2px solid #f0f0f0; border-radius: 8px; cursor: pointer; transition: all 0.2s; }
.address-card:hover { border-color: #ff6b35; }
.address-card.selected { border-color: #ff6b35; background: #fff8f5; }
.coupon-section { display: flex; flex-direction: column; gap: 12px; }
.coupon-info { padding: 12px; background: #f0f9ff; border-radius: 6px; border-left: 3px solid #3b82f6; }
.coupon-info p { margin: 4px 0; font-size: 14px; color: #333; }
</style>

