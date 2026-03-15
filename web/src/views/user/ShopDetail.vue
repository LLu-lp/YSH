<template>
  <div class="shop-detail" v-loading="loading">
    <!-- 店铺头部 -->
    <div class="shop-header">
      <div class="header-bg"></div>
      <div class="header-content">
        <el-avatar :size="80" :src="shop?.logo">{{ (shop?.name || '店').charAt(0) }}</el-avatar>
        <div class="shop-info">
          <h1 class="shop-name">{{ shop?.name }}</h1>
          <div class="shop-stats">
            <span><strong>{{ shop?.salesCount || 0 }}</strong> 销售量</span>
            <span><strong>{{ shop?.rating || 5.0 }}</strong> 评分</span>
            <span v-if="shop?.description" class="shop-desc">{{ shop?.description }}</span>
          </div>
        </div>
        <el-button type="primary" size="large" @click="handleFollow">
          {{ isFollowing ? '已关注' : '关注店铺' }}
        </el-button>
      </div>
    </div>

    <!-- 店铺优惠券 -->
    <div class="shop-coupons" v-if="coupons.length > 0">
      <div class="section-header">
        <h2>店铺优惠券</h2>
      </div>
      <div class="coupon-list">
        <div v-for="coupon in coupons" :key="coupon.id" class="coupon-card">
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
    </div>

    <!-- 店铺商品 -->
    <div class="shop-products">
      <div class="section-header">
        <h2>店铺商品</h2>
        <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px">
          <el-option label="综合排序" value="default" />
          <el-option label="销量最高" value="sales" />
          <el-option label="价格最低" value="price_asc" />
          <el-option label="价格最高" value="price_desc" />
          <el-option label="最新上架" value="newest" />
        </el-select>
      </div>

      <div class="product-grid">
        <div class="product-card" v-for="product in products" :key="product.id" @click="$router.push(`/user/products/${product.id}`)">
          <div class="product-img-wrap">
            <img :src="product.mainImage || '/placeholder.svg?height=200&width=200'" :alt="product.name" />
            <div class="product-tag" v-if="product.salesCount > 100">热销</div>
          </div>
          <div class="product-info">
            <p class="product-name">{{ product.name }}</p>
            <p class="product-desc">{{ product.description || '' }}</p>
            <div class="product-bottom">
              <span class="price">¥{{ product.price }}</span>
              <span class="sales">已售 {{ product.salesCount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="products.length === 0" class="empty-state">
        <el-empty description="暂无商品" :image-size="80" />
      </div>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadProducts"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getShopDetail, getShopProducts, getShopCoupons, followShop, unfollowShop, isFollowingShop, receiveCoupon as receiveApi } from '@/api'
import { useUserStore } from '@/stores/user.js'

const route = useRoute()
const loading = ref(false)
const shop = ref(null)
const products = ref([])
const coupons = ref([])
const sortBy = ref('default')
const currentPage = ref(1)
const pageSize = 12
const total = ref(0)
const isFollowing = ref(false)
const userStore = useUserStore()

// 监听排序变化
watch(sortBy, () => {
  currentPage.value = 1
  loadProducts()
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const loadShop = async () => {
  try {
    const res = await getShopDetail(route.params.id)
    if (res.code === 200 && res.data) {
      shop.value = res.data
    }
  } catch (error) {
    console.error('加载店铺信息失败:', error)
  }
}

const loadProducts = async () => {
  try {
    const res = await getShopProducts(route.params.id, {
      page: currentPage.value,
      pageSize: pageSize,
      sortBy: sortBy.value
    })
    if (res.code === 200 && res.data) {
      products.value = res.data.content || res.data
      total.value = res.data.totalElements || res.data.length
    }
  } catch (error) {
    console.error('加载店铺商品失败:', error)
  }
}

const loadCoupons = async () => {
  try {
    const res = await getShopCoupons(route.params.id)
    if (res.code === 200 && res.data) {
      coupons.value = Array.isArray(res.data) ? res.data : []
    }
  } catch (error) {
    console.error('加载店铺优惠券失败:', error)
  }
}

const receiveCoupon = async (couponId) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    const res = await receiveApi({ couponId })
    if (res.code === 200) {
      ElMessage.success('领取成功')
      loadCoupons()
    } else {
      ElMessage.error(res.message || '领取失败')
    }
  } catch (error) {
    console.error('领取优惠券失败:', error)
    ElMessage.error('领取失败')
  }
}

const handleFollow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    if (isFollowing.value) {
      await unfollowShop(route.params.id)
      ElMessage.success('已取消关注')
    } else {
      await followShop(route.params.id)
      ElMessage.success('已关注店铺')
    }
    isFollowing.value = !isFollowing.value
  } catch (error) {
    console.error('关注操作失败:', error)
  }
}

const checkFollowing = async () => {
  if (!userStore.isLoggedIn) {
    isFollowing.value = false
    return
  }
  
  try {
    const res = await isFollowingShop(route.params.id)
    if (res.code === 200) {
      isFollowing.value = res.data
    }
  } catch (error) {
    console.error('检查关注状态失败:', error)
  }
}

onMounted(async () => {
  loading.value = true
  try {
    await loadShop()
    await loadProducts()
    await loadCoupons()
    await checkFollowing()
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.shop-detail { background: #fff; min-height: 100vh; }
.shop-header { position: relative; margin-bottom: 40px; }
.header-bg { height: 200px; background: linear-gradient(135deg, #ff6b35, #ff8c5a); }
.header-content { max-width: 1280px; margin: 0 auto; padding: 0 24px; display: flex; align-items: flex-end; gap: 24px; transform: translateY(-40px); }
.shop-info { flex: 1; }
.shop-name { font-size: 28px; font-weight: 700; color: #333; margin-bottom: 12px; }
.shop-stats { display: flex; align-items: center; gap: 24px; font-size: 14px; color: #666; }
.shop-stats strong { font-size: 18px; color: #ff6b35; font-weight: 700; }
.shop-desc { color: #999; font-size: 13px; }
.shop-products { max-width: 1280px; margin: 0 auto; padding: 0 24px 40px; }
.shop-coupons { max-width: 1280px; margin: 0 auto; padding: 0 24px 40px; }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.section-header h2 { font-size: 20px; font-weight: 700; color: #333; }
.coupon-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(400px, 1fr)); gap: 16px; margin-bottom: 24px; }
.coupon-card { display: flex; background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 100%); border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(76, 175, 80, 0.2); transition: transform 0.2s; }
.coupon-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3); }
.coupon-left { width: 140px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #fff; border-right: 2px dashed rgba(255,255,255,0.3); padding: 20px; }
.coupon-amount { display: flex; align-items: baseline; }
.coupon-amount .symbol { font-size: 20px; font-weight: 600; }
.coupon-amount .value { font-size: 36px; font-weight: 700; }
.coupon-condition { font-size: 12px; margin-top: 8px; opacity: 0.9; }
.coupon-right { flex: 1; padding: 20px; display: flex; flex-direction: column; gap: 8px; background: #fff; }
.coupon-name { font-size: 16px; font-weight: 600; color: #333; }
.coupon-time { font-size: 13px; color: #666; }
.coupon-stock { font-size: 13px; color: #999; }
.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.product-card { border: 1px solid #f0f0f0; border-radius: 10px; overflow: hidden; cursor: pointer; transition: all 0.2s; }
.product-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); }
.product-img-wrap { position: relative; aspect-ratio: 1; overflow: hidden; background: #f8f8f8; }
.product-img-wrap img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s; }
.product-card:hover .product-img-wrap img { transform: scale(1.05); }
.product-tag { position: absolute; top: 8px; left: 8px; background: #ff6b35; color: #fff; font-size: 12px; padding: 2px 8px; border-radius: 4px; }
.product-info { padding: 12px; }
.product-name { font-size: 14px; font-weight: 600; color: #333; margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-desc { font-size: 12px; color: #999; margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-bottom { display: flex; align-items: center; justify-content: space-between; }
.price { font-size: 18px; font-weight: 700; color: #ff6b35; }
.sales { font-size: 12px; color: #bbb; }
.empty-state { text-align: center; padding: 60px 20px; }
.pagination { display: flex; justify-content: center; margin-top: 24px; }
@media (max-width: 768px) { .product-grid { grid-template-columns: repeat(2, 1fr); } }
</style>

