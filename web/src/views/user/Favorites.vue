<template>
  <div class="favorites-page">
    <div class="page-header">
      <h2>我的收藏</h2>
    </div>

    <div v-if="favoriteList.length > 0" class="product-grid" v-loading="loading">
      <div v-for="item in favoriteList" :key="item.id" class="product-card">
        <router-link :to="`/user/products/${item.id}`" class="product-link">
          <div class="product-image">
            <img :src="item.mainImage || '/placeholder.svg?height=200&width=200'" :alt="item.name" />
          </div>
          <div class="product-info">
            <div class="product-name">{{ item.name }}</div>
            <div class="product-shop">{{ item.shopName || '好物店铺' }}</div>
            <div class="product-price">¥{{ item.price }}</div>
          </div>
        </router-link>
        <div class="card-actions">
          <button @click="addToCartItem(item)" class="btn-cart">加入购物车</button>
          <button @click="removeFavoriteItem(item)" class="btn-remove">取消收藏</button>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon">♡</div>
      <p>暂无收藏商品</p>
      <router-link to="/user/products" class="btn-primary">去逛逛</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFavorites, removeFavorite, addToCart } from '@/api'

const loading = ref(false)
const favoriteList = ref([])

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavorites()
    if (res.code === 200 && res.data) {
      favoriteList.value = res.data
    }
  } catch (error) {
    console.error('加载收藏失败:', error)
  } finally {
    loading.value = false
  }
}

const addToCartItem = async (item) => {
  try {
    await addToCart({ productId: item.id, quantity: 1 })
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error('加入购物车失败:', error)
  }
}

const removeFavoriteItem = async (item) => {
  if (!confirm('确定取消收藏吗？')) return
  try {
    await removeFavorite(item.id)
    favoriteList.value = favoriteList.value.filter(f => f.id !== item.id)
    ElMessage.success('已取消收藏')
  } catch (error) {
    console.error('取消收藏失败:', error)
  }
}

onMounted(() => loadFavorites())
</script>

<style scoped>
.favorites-page { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #1f2937; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.product-card { background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.07); transition: box-shadow 0.2s; }
.product-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.12); }
.product-link { display: block; text-decoration: none; color: inherit; }
.product-image { aspect-ratio: 1; overflow: hidden; background: #f3f4f6; }
.product-image img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s; }
.product-card:hover .product-image img { transform: scale(1.05); }
.product-info { padding: 12px; }
.product-name { font-size: 14px; font-weight: 500; color: #1f2937; margin-bottom: 4px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-shop { font-size: 12px; color: #9ca3af; margin-bottom: 6px; }
.product-price { font-size: 16px; font-weight: 700; color: #ef4444; }
.card-actions { display: flex; gap: 8px; padding: 0 12px 12px; }
.btn-cart { flex: 1; padding: 7px; background: #ff6b35; color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 13px; }
.btn-remove { padding: 7px 10px; background: #fff; color: #9ca3af; border: 1px solid #e5e7eb; border-radius: 6px; cursor: pointer; font-size: 13px; }
.empty-state { text-align: center; padding: 80px 20px; }
.empty-icon { font-size: 48px; margin-bottom: 16px; color: #d1d5db; }
.empty-state p { color: #9ca3af; margin-bottom: 20px; font-size: 15px; }
.btn-primary { display: inline-block; padding: 10px 28px; background: #ff6b35; color: #fff; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; text-decoration: none; }
</style>
