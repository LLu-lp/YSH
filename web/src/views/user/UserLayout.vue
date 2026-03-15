<template>
  <div class="user-layout">
    <!-- 顶部导航 -->
    <header class="navbar">
      <div class="nav-inner">
        <div class="nav-left">
          <div class="logo" @click="$router.push('/user/home')">
            <span class="logo-icon">好</span>
            <span class="logo-text">好物购买</span>
          </div>
          <!-- 搜索框 -->
          <div class="search-bar">
            <el-input
              v-model="searchKey"
              placeholder="搜索商品..."
              size="large"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button :icon="Search" @click="handleSearch" />
              </template>
            </el-input>
          </div>
        </div>
        <div class="nav-right">
          <!-- 已登录状态 -->
          <template v-if="userStore.isLoggedIn">
          <!-- 购物车 -->
          <el-badge :value="cartCount > 0 ? cartCount : ''" class="cart-badge">
            <el-button :icon="ShoppingCart" circle size="large" @click="$router.push('/user/cart')" />
          </el-badge>
          <!-- 用户菜单 -->
          <el-dropdown @command="handleCommand">
            <div class="user-info">
                <el-avatar :size="36" :src="userAvatar">
                {{ userStore.userInfo?.username?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon> 个人信息</el-dropdown-item>
                <el-dropdown-item command="orders"><el-icon><List /></el-icon> 我的订单</el-dropdown-item>
                <el-dropdown-item command="address"><el-icon><Location /></el-icon> 收货地址</el-dropdown-item>
                <el-dropdown-item command="favorites"><el-icon><Star /></el-icon> 我的收藏</el-dropdown-item>
                <el-dropdown-item command="coupons"><el-icon><Ticket /></el-icon> 优惠券</el-dropdown-item>
                <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon> 退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          </template>
          <!-- 未登录状态 -->
          <template v-else>
            <el-button type="primary" @click="$router.push('/user/login')">登录</el-button>
            <el-button @click="$router.push('/user/register')">注册</el-button>
          </template>
        </div>
      </div>
      <!-- 分类导航 -->
      <div class="category-nav">
        <div class="category-inner">
          <span
            v-for="cat in categories"
            :key="cat.id"
            class="cat-item"
            :class="{ active: activeCat === cat.id }"
            @click="handleCatClick(cat)"
          >
            {{ cat.name }}
          </span>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-inner">
        <span>© 2025 好物购买系统 | 优质商品，品质生活</span>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ShoppingCart, ArrowDown, User, List, Location, Star, Ticket, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user.js'
// TODO: import { getCategories, getCartList } from '@/api/index.js'

const router = useRouter()
const userStore = useUserStore()
const searchKey = ref('')
const cartCount = ref(0)
const activeCat = ref(null)

// 转换头像 URL
const convertAvatarUrl = (avatarUrl) => {
  if (!avatarUrl) return ''
  // 如果是完整 URL，直接返回
  if (avatarUrl.startsWith('http')) return avatarUrl
  // 如果是 /uploads/avatar/xxx.jpg 格式，转换为 /api/files/avatar/xxx.jpg
  if (avatarUrl.startsWith('/uploads/avatar/')) {
    const filename = avatarUrl.replace('/uploads/avatar/', '')
    return `/api/files/avatar/${filename}`
  }
  return avatarUrl
}

// 计算用户头像 URL
const userAvatar = computed(() => {
  return convertAvatarUrl(userStore.userInfo?.avatar)
})

// 模拟分类数据 - TODO: 替换为接口数据
const categories = ref([
  { id: 1, name: '全部商品' },
  { id: 2, name: '数码电子' },
  { id: 3, name: '服装鞋包' },
  { id: 4, name: '美妆护肤' },
  { id: 5, name: '家居生活' },
  { id: 6, name: '食品饮料' },
  { id: 7, name: '图书文具' },
  { id: 8, name: '运动户外' }
])

const handleSearch = () => {
  router.push({ path: '/user/products', query: { keyword: searchKey.value } })
}

const handleCatClick = (cat) => {
  activeCat.value = cat.id
  router.push({ path: '/user/products', query: { categoryId: cat.id } })
}

const handleCommand = async (cmd) => {
  if (cmd === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    userStore.logout()
    router.push('/user/login')
  } else {
    router.push(`/user/${cmd}`)
  }
}

onMounted(async () => {
  // TODO: 获取购物车数量
  // const res = await getCartList()
  // cartCount.value = res.data.length
  // TODO: 获取分类
  // const catsRes = await getCategories()
  // categories.value = [{ id: null, name: '全部商品' }, ...catsRes.data]
})
</script>

<style scoped>
.user-layout { min-height:100vh; display:flex; flex-direction:column; background:#f5f5f5; }
.navbar { background:#fff; box-shadow:0 2px 8px rgba(0,0,0,0.08); position:sticky; top:0; z-index:100; }
.nav-inner { max-width:1280px; margin:0 auto; padding:16px 24px; display:flex; align-items:center; justify-content:space-between; gap:20px; }
.nav-left { display:flex; align-items:center; gap:24px; flex:1; }
.logo { display:flex; align-items:center; gap:8px; cursor:pointer; flex-shrink:0; }
.logo-icon { width:36px; height:36px; background:#ff6b35; border-radius:8px; color:#fff; font-size:18px; font-weight:800; display:flex; align-items:center; justify-content:center; }
.logo-text { font-size:20px; font-weight:700; color:#ff6b35; white-space:nowrap; }
.search-bar { flex:1; max-width:500px; }
.nav-right { display:flex; align-items:center; gap:16px; }
.cart-badge :deep(.el-badge__content) { background:#ff6b35; }
.user-info { display:flex; align-items:center; gap:8px; cursor:pointer; padding:4px 8px; border-radius:8px; }
.user-info:hover { background:#f5f5f5; }
.username { font-size:14px; color:#333; max-width:80px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.category-nav { border-top:1px solid #f0f0f0; background:#fff; }
.category-inner { max-width:1280px; margin:0 auto; padding:0 24px; display:flex; gap:4px; }
.cat-item { padding:10px 16px; font-size:14px; color:#666; cursor:pointer; border-bottom:2px solid transparent; transition:all 0.2s; }
.cat-item:hover { color:#ff6b35; }
.cat-item.active { color:#ff6b35; border-bottom-color:#ff6b35; font-weight:600; }
.main-content { flex:1; max-width:1280px; margin:0 auto; width:100%; padding:24px; }
.footer { background:#333; color:#999; padding:24px; text-align:center; font-size:13px; margin-top:auto; }
</style>
