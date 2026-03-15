<template>
  <div class="product-detail" v-loading="loading">
    <div v-if="product">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/user/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/user/products' }">商品列表</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="detail-main">
        <div class="img-section">
          <div class="main-img">
            <img :src="currentImg || product.mainImage || '/placeholder.svg?height=450&width=450'" :alt="product.name" />
          </div>
          <div class="img-thumbs" v-if="imageList.length > 0">
            <img
              v-for="(img, idx) in imageList"
              :key="idx"
              :src="img"
              :class="{ active: currentImg === img }"
              @click="currentImg = img"
            />
          </div>
        </div>

        <div class="info-section">
          <h1 class="product-name">{{ product.name }}</h1>
          <p class="product-desc">{{ product.description }}</p>

          <div class="price-block">
            <span class="price">¥{{ product.price }}</span>
            <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
          </div>

          <div class="stats-row">
            <span>已售 {{ product.salesCount || 0 }}</span>
            <span>库存 {{ product.stock }}</span>
            <span>评分 {{ product.rating || 5.0 }}</span>
          </div>

          <div class="quantity-row">
            <span class="qty-label">数量：</span>
            <el-input-number v-model="quantity" :min="1" :max="product.stock" size="large" />
          </div>

          <div class="action-row">
            <el-button size="large" :icon="ShoppingCart" @click="handleAddCart" :loading="cartLoading">
              加入购物车
            </el-button>
            <el-button type="primary" size="large" @click="handleBuyNow">
              立即购买
            </el-button>
            <el-button :icon="Star" circle size="large" :type="isFavorite ? 'warning' : ''" @click="toggleFavorite" />
          </div>

          <div class="shop-card">
            <el-avatar :size="40">{{ (product.shopName || '店').charAt(0) }}</el-avatar>
            <div class="shop-info">
              <p class="shop-name">{{ product.shopName || '官方旗舰店' }}</p>
              <p class="shop-desc">正品保障 · 7天无理由退换</p>
            </div>
            <el-button size="small" @click="$router.push(`/user/shop/${product.shopId}`)">进店逛逛</el-button>
          </div>
        </div>
      </div>

      <div class="detail-tabs">
        <el-tabs>
          <el-tab-pane label="商品详情">
            <div class="detail-content">
              <p v-if="product.description">{{ product.description }}</p>
              <p v-else>商品详情暂无描述</p>
            </div>
          </el-tab-pane>
          <el-tab-pane label="规格参数">
            <div class="specs-content" v-if="specsList.length > 0">
              <div v-for="(spec, idx) in specsList" :key="idx" class="spec-item">
                <span class="spec-key">{{ spec.key }}</span>
                <span class="spec-value">{{ spec.value }}</span>
              </div>
            </div>
            <el-empty v-else description="暂无规格参数" :image-size="80" />
          </el-tab-pane>
          <el-tab-pane label="用户评价">
            <div class="reviews" v-if="reviews.length > 0">
              <div class="review-item" v-for="review in reviews" :key="review.id">
                <el-avatar :size="36" :src="review.userAvatar">{{ (review.userName || '用').charAt(0) }}</el-avatar>
                <div class="review-content">
                  <div class="review-header">
                    <span class="review-user">{{ review.userName || '匿名用户' }}</span>
                    <el-rate :model-value="review.rating" disabled size="small" />
                    <span class="review-date">{{ formatDateTime(review.createTime) }}</span>
                  </div>
                  <p>{{ review.content }}</p>
                  <div class="review-images" v-if="review.images">
                    <img v-for="(img, idx) in parseImages(review.images)" :key="idx" :src="img" />
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无评价" :image-size="80" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Star } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user.js'
import { getProductDetail, addToCart, addFavorite, removeFavorite, getFavorites } from '@/api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const cartLoading = ref(false)
const isFavorite = ref(false)
const quantity = ref(1)
const currentImg = ref('')

const product = ref(null)
const reviews = ref([])

const imageList = computed(() => {
  if (!product.value?.images) return []
  try {
    const imgs = JSON.parse(product.value.images)
    return Array.isArray(imgs) ? imgs : []
  } catch {
    return []
  }
})

const specsList = computed(() => {
  if (!product.value?.specs) return []
  try {
    const specs = JSON.parse(product.value.specs)
    if (typeof specs === 'object' && !Array.isArray(specs)) {
      return Object.entries(specs).map(([key, value]) => ({ key, value }))
    }
    return Array.isArray(specs) ? specs : []
  } catch {
    return []
  }
})

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

const parseImages = (images) => {
  if (!images) return []
  try {
    const imgs = JSON.parse(images)
    return Array.isArray(imgs) ? imgs : []
  } catch {
    return []
  }
}

const loadProduct = async () => {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    if (res.code === 200 && res.data) {
      product.value = res.data
      currentImg.value = res.data.mainImage || ''
      if (res.data.reviews) {
        reviews.value = res.data.reviews
      }
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
  } finally {
    loading.value = false
  }
}

const checkFavorite = async () => {
  // 未登录时不检查收藏状态
  if (!userStore.isLoggedIn) {
    isFavorite.value = false
    return
  }
  
  try {
    const res = await getFavorites()
    if (res.code === 200 && res.data) {
      const favs = Array.isArray(res.data) ? res.data : res.data.content || []
      isFavorite.value = favs.some(f => f.id === product.value?.id)
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

const handleAddCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/user/login')
    return
  }
  
  if (!product.value) return
  cartLoading.value = true
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error('加入购物车失败:', error)
  } finally {
    cartLoading.value = false
  }
}

const handleBuyNow = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/user/login')
    return
  }
  
  if (!product.value) return
  router.push({ path: '/user/checkout', query: { action: 'buy', productId: product.value.id, quantity: quantity.value } })
}

const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/user/login')
    return
  }
  
  if (!product.value) return
  try {
    if (isFavorite.value) {
      await removeFavorite(product.value.id)
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite({ productId: product.value.id })
      ElMessage.success('已收藏')
    }
    isFavorite.value = !isFavorite.value
  } catch (error) {
    console.error('收藏操作失败:', error)
  }
}

onMounted(() => {
  loadProduct().then(() => {
    if (product.value) {
      checkFavorite()
    }
  })
})
</script>

<style scoped>
.product-detail { background: #fff; border-radius: 12px; padding: 24px; }
.breadcrumb { margin-bottom: 20px; }
.detail-main { display: flex; gap: 40px; margin-bottom: 40px; }
.img-section { flex-shrink: 0; width: 450px; }
.main-img { width: 450px; height: 450px; border-radius: 10px; overflow: hidden; border: 1px solid #f0f0f0; }
.main-img img { width: 100%; height: 100%; object-fit: cover; }
.img-thumbs { display: flex; gap: 8px; margin-top: 12px; flex-wrap: wrap; }
.img-thumbs img { width: 64px; height: 64px; object-fit: cover; border-radius: 6px; border: 2px solid transparent; cursor: pointer; }
.img-thumbs img.active { border-color: #ff6b35; }
.info-section { flex: 1; }
.product-name { font-size: 22px; font-weight: 700; color: #333; margin-bottom: 8px; }
.product-desc { font-size: 14px; color: #999; margin-bottom: 20px; }
.price-block { display: flex; align-items: center; gap: 12px; background: #fff8f5; padding: 16px 20px; border-radius: 8px; margin-bottom: 20px; }
.price { font-size: 32px; font-weight: 700; color: #ff6b35; }
.original-price { font-size: 16px; color: #bbb; text-decoration: line-through; }
.stats-row { display: flex; gap: 24px; font-size: 13px; color: #999; margin-bottom: 20px; }
.quantity-row { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.qty-label { font-size: 14px; color: #666; }
.action-row { display: flex; gap: 12px; margin-bottom: 24px; }
.shop-card { display: flex; align-items: center; gap: 12px; border: 1px solid #f0f0f0; border-radius: 10px; padding: 16px; }
.shop-name { font-size: 14px; font-weight: 600; color: #333; }
.shop-desc { font-size: 12px; color: #999; }
.shop-info { flex: 1; }
.detail-tabs { margin-top: 8px; }
.detail-content { padding: 24px 0; line-height: 1.8; }
.specs-content { display: flex; flex-direction: column; gap: 8px; }
.spec-item { display: flex; padding: 12px 16px; background: #f9fafb; border-radius: 6px; }
.spec-key { width: 120px; color: #666; font-weight: 500; }
.spec-value { flex: 1; color: #333; }
.reviews { display: flex; flex-direction: column; gap: 20px; padding: 16px 0; }
.review-item { display: flex; gap: 16px; }
.review-content { flex: 1; }
.review-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.review-user { font-size: 14px; font-weight: 600; }
.review-date { font-size: 12px; color: #999; margin-left: auto; }
</style>
