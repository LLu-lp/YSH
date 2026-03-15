<template>
  <div class="coupons-page">
    <el-card class="page-header">
      <h2>我的优惠券</h2>
    </el-card>

    <el-tabs v-model="activeTab" class="coupon-tabs">
      <el-tab-pane label="未使用" name="unused">
        <div v-if="unusedCoupons.length > 0" class="coupon-list">
          <div v-for="coupon in unusedCoupons" :key="coupon.id" class="coupon-card">
            <div class="coupon-left">
              <div class="coupon-amount">
                <span class="symbol">¥</span>
                <span class="value">{{ coupon.value }}</span>
              </div>
              <div class="coupon-condition">满{{ coupon.minAmount }}可用</div>
            </div>
            <div class="coupon-right">
              <div class="coupon-name">{{ coupon.name }}</div>
              <div class="coupon-time">
                有效期：{{ formatDate(coupon.startTime) }} - {{ formatDate(coupon.endTime) }}
              </div>
              <el-button type="primary" size="small" @click="goShopping">去使用</el-button>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无未使用的优惠券" />
      </el-tab-pane>

      <el-tab-pane label="已使用" name="used">
        <div v-if="usedCoupons.length > 0" class="coupon-list">
          <div v-for="coupon in usedCoupons" :key="coupon.id" class="coupon-card used">
            <div class="coupon-left">
              <div class="coupon-amount">
                <span class="symbol">¥</span>
                <span class="value">{{ coupon.value }}</span>
              </div>
              <div class="coupon-condition">满{{ coupon.minAmount }}可用</div>
            </div>
            <div class="coupon-right">
              <div class="coupon-name">{{ coupon.name }}</div>
              <div class="coupon-time">
                使用时间：{{ formatDate(coupon.usedTime) }}
              </div>
              <div class="coupon-status">已使用</div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无已使用的优惠券" />
      </el-tab-pane>

      <el-tab-pane label="已过期" name="expired">
        <div v-if="expiredCoupons.length > 0" class="coupon-list">
          <div v-for="coupon in expiredCoupons" :key="coupon.id" class="coupon-card expired">
            <div class="coupon-left">
              <div class="coupon-amount">
                <span class="symbol">¥</span>
                <span class="value">{{ coupon.value }}</span>
              </div>
              <div class="coupon-condition">满{{ coupon.minAmount }}可用</div>
            </div>
            <div class="coupon-right">
              <div class="coupon-name">{{ coupon.name }}</div>
              <div class="coupon-time">
                过期时间：{{ formatDate(coupon.endTime) }}
              </div>
              <div class="coupon-status">已过期</div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无过期的优惠券" />
      </el-tab-pane>
    </el-tabs>

    <!-- 领取优惠券区域 -->
    <el-card class="receive-section">
      <h3>可领取的优惠券</h3>
      <div v-if="availableCoupons.length > 0" class="coupon-list">
        <div v-for="coupon in availableCoupons" :key="coupon.id" class="coupon-card available">
          <div class="coupon-left">
            <div class="coupon-amount">
              <span class="symbol">¥</span>
              <span class="value">{{ coupon.value }}</span>
            </div>
            <div class="coupon-condition">满{{ coupon.minAmount }}可用</div>
          </div>
          <div class="coupon-right">
            <div class="coupon-name">{{ coupon.name }}</div>
            <div class="coupon-time">
              有效期：{{ formatDate(coupon.startTime) }} - {{ formatDate(coupon.endTime) }}
            </div>
            <div class="coupon-stock">剩余：{{ coupon.remainingCount }}/{{ coupon.totalCount }}</div>
            <el-button type="primary" size="small" @click="receiveCoupon(coupon.id)">立即领取</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无可领取的优惠券" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserCoupons, getAvailableCoupons, receiveCoupon as receiveApi } from '@/api/index.js'

const router = useRouter()
const activeTab = ref('unused')
const unusedCoupons = ref([])
const usedCoupons = ref([])
const expiredCoupons = ref([])
const availableCoupons = ref([])

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const goShopping = () => {
  router.push('/user/products')
}

const loadMyCoupons = async () => {
  try {
    const res = await getUserCoupons()
    if (res.code === 200) {
      const now = new Date()
      unusedCoupons.value = res.data.filter(c => c.status === 1 && new Date(c.endTime) > now)
      usedCoupons.value = res.data.filter(c => c.status === 2)
      expiredCoupons.value = res.data.filter(c => c.status === 1 && new Date(c.endTime) <= now)
    }
  } catch (err) {
    console.error('加载优惠券失败', err)
  }
}

const loadAvailableCoupons = async () => {
  try {
    const res = await getAvailableCoupons()
    if (res.code === 200) {
      availableCoupons.value = res.data
    }
  } catch (err) {
    console.error('加载可领取优惠券失败', err)
  }
}

const receiveCoupon = async (couponId) => {
  try {
    const res = await receiveApi({ couponId })
    if (res.code === 200) {
      ElMessage.success('领取成功')
      loadMyCoupons()
      loadAvailableCoupons()
    } else {
      ElMessage.error(res.message || '领取失败')
    }
  } catch (err) {
    ElMessage.error('领取失败')
  }
}

onMounted(() => {
  loadMyCoupons()
  loadAvailableCoupons()
})
</script>

<style scoped>
.coupons-page { max-width: 1000px; margin: 0 auto; }
.page-header { margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 24px; color: #333; }
.coupon-tabs { background: #fff; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
.coupon-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(400px, 1fr)); gap: 16px; }
.coupon-card { display: flex; background: linear-gradient(135deg, #ff6b35 0%, #ff8c5a 100%); border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(255,107,53,0.2); transition: transform 0.2s; }
.coupon-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(255,107,53,0.3); }
.coupon-card.used, .coupon-card.expired { background: linear-gradient(135deg, #ccc 0%, #ddd 100%); opacity: 0.7; }
.coupon-card.available { background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 100%); }
.coupon-left { width: 140px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #fff; border-right: 2px dashed rgba(255,255,255,0.3); padding: 20px; }
.coupon-amount { display: flex; align-items: baseline; }
.coupon-amount .symbol { font-size: 20px; font-weight: 600; }
.coupon-amount .value { font-size: 36px; font-weight: 700; }
.coupon-condition { font-size: 12px; margin-top: 8px; opacity: 0.9; }
.coupon-right { flex: 1; padding: 20px; display: flex; flex-direction: column; gap: 8px; background: #fff; }
.coupon-name { font-size: 16px; font-weight: 600; color: #333; }
.coupon-time { font-size: 13px; color: #666; }
.coupon-stock { font-size: 13px; color: #999; }
.coupon-status { font-size: 14px; color: #999; font-weight: 500; }
.receive-section { margin-top: 20px; }
.receive-section h3 { margin: 0 0 20px 0; font-size: 18px; color: #333; }
</style>

