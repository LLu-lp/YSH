<template>
  <div class="product-list-page">
    <aside class="filter-sidebar">
      <div class="filter-section">
        <h4 class="filter-title">商品分类</h4>
        <el-menu :default-active="String(activeCategory)" @select="handleCatSelect">
          <el-menu-item index="0">全部商品</el-menu-item>
          <el-menu-item v-for="cat in categories" :key="cat.id" :index="String(cat.id)">
            {{ cat.name }}
          </el-menu-item>
        </el-menu>
      </div>
      <div class="filter-section">
        <h4 class="filter-title">价格区间</h4>
        <div class="price-range">
          <el-input v-model="priceMin" placeholder="最低价" size="small" />
          <span>—</span>
          <el-input v-model="priceMax" placeholder="最高价" size="small" />
        </div>
        <el-button size="small" type="primary" style="width:100%;margin-top:10px" @click="loadProducts">确定</el-button>
      </div>
    </aside>

    <div class="product-main">
      <div class="search-tip" v-if="keyword">
        搜索 "<strong>{{ keyword }}</strong>" 的结果，共 {{ total }} 件商品
      </div>

      <div class="sort-bar">
        <span class="sort-label">排序：</span>
        <el-button-group size="small">
          <el-button :type="sortBy === 'default' ? 'primary' : ''" @click="sortBy = 'default'; loadProducts()">默认</el-button>
          <el-button :type="sortBy === 'salesCount' ? 'primary' : ''" @click="sortBy = 'salesCount'; loadProducts()">销量</el-button>
          <el-button :type="sortBy === 'price' && sortOrder === 'asc' ? 'primary' : ''" @click="sortBy = 'price'; sortOrder = 'asc'; loadProducts()">价格↑</el-button>
          <el-button :type="sortBy === 'price' && sortOrder === 'desc' ? 'primary' : ''" @click="sortBy = 'price'; sortOrder = 'desc'; loadProducts()">价格↓</el-button>
          <el-button :type="sortBy === 'createTime' ? 'primary' : ''" @click="sortBy = 'createTime'; loadProducts()">最新</el-button>
        </el-button-group>
        <span class="total-text">共 {{ total }} 件</span>
      </div>

      <div class="product-grid" v-loading="loading">
        <div class="product-card" v-for="product in products" :key="product.id" @click="$router.push(`/user/products/${product.id}`)">
          <div class="product-img-wrap">
            <img :src="product.mainImage || '/placeholder.svg?height=220&width=220'" :alt="product.name" />
            <div class="product-tag" v-if="product.salesCount > 100">热销</div>
          </div>
          <div class="product-info">
            <p class="product-name">{{ product.name }}</p>
            <p class="product-desc">{{ product.description || '' }}</p>
            <div class="product-bottom">
              <span class="price">¥{{ product.price }}</span>
              <span class="sales">已售{{ product.salesCount || 0 }}</span>
            </div>
          </div>
        </div>
        <div v-if="products.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无商品" />
        </div>
      </div>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, sizes"
          :page-sizes="[12, 24, 48]"
          @change="loadProducts"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProducts, getCategories } from '@/api'

const route = useRoute()
const loading = ref(false)
const products = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(12)
const keyword = ref(route.query.keyword || '')
const activeCategory = ref(route.query.categoryId || '0')
const sortBy = ref('default')
const sortOrder = ref('desc')
const priceMin = ref('')
const priceMax = ref('')

const categories = ref([])

const handleCatSelect = (id) => {
  activeCategory.value = id
  page.value = 1
  loadProducts()
}

const loadCategories = async () => {
  try {
    const res = await getCategories()
    if (res.code === 200 && res.data) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    const params = { 
      page: page.value, 
      pageSize: pageSize.value, 
      keyword: keyword.value || undefined,
      categoryId: activeCategory.value !== '0' ? activeCategory.value : undefined,
      sortBy: sortBy.value !== 'default' ? sortBy.value : undefined,
      sortOrder: sortOrder.value
    }
    const res = await getProducts(params)
    if (res.code === 200 && res.data) {
      if (res.data.content) {
        products.value = res.data.content
        total.value = res.data.totalElements || 0
      } else if (Array.isArray(res.data)) {
        products.value = res.data
        total.value = res.data.length
      }
    }
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

watch(() => route.query, (q) => {
  keyword.value = q.keyword || ''
  activeCategory.value = q.categoryId || '0'
  loadProducts()
})

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>

<style scoped>
.product-list-page { display:flex; gap:20px; align-items:flex-start; }
.filter-sidebar { width:200px; flex-shrink:0; background:#fff; border-radius:10px; overflow:hidden; }
.filter-section { padding:16px; border-bottom:1px solid #f0f0f0; }
.filter-title { font-size:14px; font-weight:700; color:#333; margin-bottom:12px; }
.price-range { display:flex; align-items:center; gap:8px; }
.product-main { flex:1; min-width:0; }
.search-tip { background:#fff3ee; border-radius:8px; padding:12px 16px; margin-bottom:16px; font-size:14px; color:#666; }
.sort-bar { background:#fff; border-radius:8px; padding:12px 16px; margin-bottom:16px; display:flex; align-items:center; gap:16px; }
.sort-label { font-size:14px; color:#666; }
.total-text { margin-left:auto; font-size:13px; color:#999; }
.product-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:16px; min-height:300px; }
.product-card { background:#fff; border-radius:10px; overflow:hidden; cursor:pointer; transition:all 0.2s; border:1px solid #f0f0f0; }
.product-card:hover { transform:translateY(-4px); box-shadow:0 8px 24px rgba(0,0,0,0.1); }
.product-img-wrap { position:relative; aspect-ratio:1; overflow:hidden; background:#f8f8f8; }
.product-img-wrap img { width:100%; height:100%; object-fit:cover; transition:transform 0.3s; }
.product-card:hover .product-img-wrap img { transform:scale(1.05); }
.product-tag { position:absolute; top:8px; left:8px; background:#ff6b35; color:#fff; font-size:11px; padding:2px 6px; border-radius:4px; }
.product-info { padding:12px; }
.product-name { font-size:14px; font-weight:600; color:#333; margin-bottom:4px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.product-desc { font-size:12px; color:#999; margin-bottom:8px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.product-bottom { display:flex; align-items:center; justify-content:space-between; }
.price { font-size:18px; font-weight:700; color:#ff6b35; }
.sales { font-size:12px; color:#bbb; }
.pagination-wrap { display:flex; justify-content:center; margin-top:32px; }
.empty-state { grid-column: 1 / -1; padding: 40px 0; }
</style>
