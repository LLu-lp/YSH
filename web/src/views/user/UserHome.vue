<template>
  <div class="user-home">
    <section class="banner-section">
      <el-carousel height="420px" :autoplay="true" :interval="4000" arrow="always">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" :style="{ background: banner.bgColor || 'linear-gradient(135deg,#ff6b35,#ff8c5a)' }">
            <div class="banner-content">
              <h2>{{ banner.title }}</h2>
              <p>{{ banner.subtitle || '' }}</p>
              <el-button type="primary" size="large" round @click="$router.push(banner.linkUrl || '/user/products')">
                立即抢购
              </el-button>
            </div>
            <img v-if="banner.imageUrl" :src="banner.imageUrl" alt="banner" class="banner-img" />
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <section class="quick-entry">
      <div class="entry-item" v-for="entry in quickEntries" :key="entry.label" @click="$router.push(entry.path)">
        <div class="entry-icon" :style="{ background: entry.bg }">
          <el-icon size="24" :style="{ color: entry.color }"><component :is="entry.icon" /></el-icon>
        </div>
        <span>{{ entry.label }}</span>
      </div>
    </section>

    <section class="product-section">
      <div class="section-header">
        <h3 class="section-title">为你推荐</h3>
        <el-link type="primary" @click="$router.push('/user/products')">查看全部</el-link>
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
        <p>暂无商品</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ShoppingCart, Star, Ticket, List, Location, GoodsFilled } from '@element-plus/icons-vue'
import { getBanners, getProducts } from '@/api'

const banners = ref([
  { id: 1, title: '新春特惠，全场8折', subtitle: '精选好物，品质生活从这里开始', bgColor: 'linear-gradient(135deg,#ff6b35,#ff8c5a)', imageUrl: '' }
])

const quickEntries = [
  { label: '我的订单', path: '/user/orders', icon: List, bg: '#fff3ee', color: '#ff6b35' },
  { label: '购物车', path: '/user/cart', icon: ShoppingCart, bg: '#fff3ee', color: '#ff6b35' },
  { label: '我的收藏', path: '/user/favorites', icon: Star, bg: '#fdf6e3', color: '#e6a23c' },
  { label: '优惠券', path: '/user/coupons', icon: Ticket, bg: '#f0f9ff', color: '#409eff' },
  { label: '收货地址', path: '/user/address', icon: Location, bg: '#f0fff4', color: '#67c23a' },
  { label: '个人中心', path: '/user/profile', icon: GoodsFilled, bg: '#fef0f0', color: '#f56c6c' }
]

const products = ref([])

const loadBanners = async () => {
  try {
    const res = await getBanners()
    if (res.code === 200 && res.data) {
      banners.value = res.data.map(b => ({
        id: b.id,
        title: b.title,
        subtitle: b.subtitle || '',
        bgColor: 'linear-gradient(135deg,#ff6b35,#ff8c5a)',
        imageUrl: b.imageUrl,
        linkUrl: b.linkUrl
      }))
    }
  } catch (error) {
    console.error('加载轮播图失败:', error)
  }
}

const loadProducts = async () => {
  try {
    const res = await getProducts({ page: 1, pageSize: 8 })
    if (res.code === 200 && res.data?.content) {
      products.value = res.data.content
    } else if (res.code === 200 && Array.isArray(res.data)) {
      products.value = res.data
    }
  } catch (error) {
    console.error('加载商品失败:', error)
  }
}

onMounted(() => {
  loadBanners()
  loadProducts()
})
</script>

<style scoped>
.user-home { padding-bottom: 40px; }
.banner-section { border-radius: 12px; overflow: hidden; margin-bottom: 24px; }
.banner-item { height: 420px; display: flex; align-items: center; justify-content: space-between; padding: 0 80px; position: relative; }
.banner-content { color: #fff; z-index: 1; }
.banner-content h2 { font-size: 40px; font-weight: 800; margin-bottom: 16px; text-shadow: 0 2px 8px rgba(0,0,0,0.2); }
.banner-content p { font-size: 18px; margin-bottom: 32px; opacity: 0.9; }
.banner-img { max-height: 300px; object-fit: contain; }
.quick-entry { display:flex; gap:16px; justify-content:center; background:#fff; border-radius:12px; padding:24px 40px; margin-bottom:24px; flex-wrap:wrap; }
.entry-item { display:flex; flex-direction:column; align-items:center; gap:8px; cursor:pointer; min-width:80px; }
.entry-item:hover { transform:translateY(-2px); }
.entry-icon { width:52px; height:52px; border-radius:12px; display:flex; align-items:center; justify-content:center; }
.entry-item span { font-size:13px; color:#666; }
.product-section { background:#fff; border-radius:12px; padding:24px; }
.section-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:20px; }
.section-title { font-size:20px; font-weight:700; color:#333; }
.product-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:16px; }
.product-card { border:1px solid #f0f0f0; border-radius:10px; overflow:hidden; cursor:pointer; transition:all 0.2s; }
.product-card:hover { transform:translateY(-4px); box-shadow:0 8px 24px rgba(0,0,0,0.1); }
.product-img-wrap { position:relative; aspect-ratio:1; overflow:hidden; background:#f8f8f8; }
.product-img-wrap img { width:100%; height:100%; object-fit:cover; transition:transform 0.3s; }
.product-card:hover .product-img-wrap img { transform:scale(1.05); }
.product-tag { position:absolute; top:8px; left:8px; background:#ff6b35; color:#fff; font-size:12px; padding:2px 8px; border-radius:4px; }
.product-info { padding:12px; }
.product-name { font-size:14px; font-weight:600; color:#333; margin-bottom:4px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.product-desc { font-size:12px; color:#999; margin-bottom:8px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.product-bottom { display:flex; align-items:center; justify-content:space-between; }
.price { font-size:18px; font-weight:700; color:#ff6b35; }
.sales { font-size:12px; color:#bbb; }
.empty-state { text-align: center; padding: 40px; color: #999; }
@media (max-width:768px) { .product-grid { grid-template-columns:repeat(2,1fr); } }
</style>
