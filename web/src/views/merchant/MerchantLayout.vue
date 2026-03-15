<template>
  <div class="merchant-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="brand">
          <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="32" height="32" rx="8" fill="#f97316"/>
            <path d="M8 22l3-10h10l3 10H8z" fill="white" opacity="0.9"/>
            <circle cx="16" cy="11" r="3" fill="white"/>
          </svg>
          <span v-if="!sidebarCollapsed" class="brand-name">商家中心</span>
        </div>
        <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path v-if="!sidebarCollapsed" d="M15 19l-7-7 7-7"/>
            <path v-else d="M9 5l7 7-7 7"/>
          </svg>
        </button>
      </div>
      <nav class="sidebar-nav">
        <div class="nav-section">
          <p v-if="!sidebarCollapsed" class="nav-label">核心功能</p>
          <router-link to="/merchant/dashboard" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
            <span v-if="!sidebarCollapsed">数据概览</span>
          </router-link>
          <router-link to="/merchant/shop" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
            <span v-if="!sidebarCollapsed">店铺管理</span>
          </router-link>
          <router-link to="/merchant/products" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 01-8 0"/></svg>
            <span v-if="!sidebarCollapsed">商品管理</span>
          </router-link>
          <router-link to="/merchant/orders" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
            <span v-if="!sidebarCollapsed">订单管理</span>
          </router-link>
          <router-link to="/merchant/coupons" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><circle cx="7" cy="15" r="1.5"/><circle cx="17" cy="15" r="1.5"/><path d="M7 10h10"/></svg>
            <span v-if="!sidebarCollapsed">优惠券管理</span>
          </router-link>
          <router-link to="/merchant/customers" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            <span v-if="!sidebarCollapsed">用户管理</span>
          </router-link>
        </div>
      </nav>
      <div class="sidebar-footer">
        <div class="merchant-info" v-if="!sidebarCollapsed">
          <div class="merchant-avatar">{{ merchantStore.merchantInfo?.shopName?.charAt(0) || 'M' }}</div>
            <div>
              <p class="merchant-name">{{ merchantStore.merchantInfo?.shopName || '我的店铺' }}</p>
              <p class="merchant-role">商家</p>
            </div>
        </div>
        <button class="logout-btn" @click="handleLogout" :title="sidebarCollapsed ? '退出登录' : ''">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
          <span v-if="!sidebarCollapsed">退出登录</span>
        </button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMerchantStore } from '@/stores/user'

const router = useRouter()
const merchantStore = useMerchantStore()
const sidebarCollapsed = ref(false)

const handleLogout = () => {
  merchantStore.logout()
  router.push('/merchant/login')
}
</script>

<style scoped>
.merchant-layout { display: flex; min-height: 100vh; background: #f5f5f4; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.sidebar { width: 220px; background: white; display: flex; flex-direction: column; border-right: 1px solid #e7e5e4; transition: width 0.25s ease; flex-shrink: 0; }
.sidebar.collapsed { width: 64px; }
.sidebar-header { padding: 16px 12px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #f5f5f4; }
.brand { display: flex; align-items: center; gap: 10px; overflow: hidden; }
.brand svg { width: 32px; height: 32px; flex-shrink: 0; }
.brand-name { font-size: 15px; font-weight: 700; color: #1c1917; white-space: nowrap; }
.collapse-btn { background: none; border: none; cursor: pointer; color: #a8a29e; padding: 4px; border-radius: 6px; display: flex; }
.collapse-btn:hover { background: #f5f5f4; color: #1c1917; }
.collapse-btn svg { width: 18px; height: 18px; }
.sidebar-nav { flex: 1; padding: 16px 8px; overflow-y: auto; }
.nav-label { font-size: 11px; color: #a8a29e; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; padding: 0 8px; margin: 0 0 6px; }
.nav-item { display: flex; align-items: center; gap: 10px; padding: 9px 10px; border-radius: 8px; color: #78716c; text-decoration: none; font-size: 14px; font-weight: 500; transition: all 0.15s; margin-bottom: 2px; }
.nav-item svg { width: 18px; height: 18px; flex-shrink: 0; }
.nav-item:hover { background: #fff7ed; color: #f97316; }
.nav-item.active { background: #fff7ed; color: #f97316; font-weight: 600; }
.sidebar-footer { padding: 12px 8px; border-top: 1px solid #f5f5f4; }
.merchant-info { display: flex; align-items: center; gap: 10px; padding: 8px 10px; margin-bottom: 4px; }
.merchant-avatar { width: 32px; height: 32px; background: #f97316; border-radius: 8px; color: white; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 700; flex-shrink: 0; }
.merchant-name { font-size: 13px; font-weight: 600; color: #1c1917; margin: 0; }
.merchant-role { font-size: 11px; color: #a8a29e; margin: 0; }
.logout-btn { display: flex; align-items: center; gap: 10px; width: 100%; padding: 9px 10px; background: none; border: none; border-radius: 8px; color: #78716c; font-size: 14px; cursor: pointer; transition: all 0.15s; }
.logout-btn svg { width: 18px; height: 18px; flex-shrink: 0; }
.logout-btn:hover { background: #fef2f2; color: #ef4444; }
.main-content { flex: 1; overflow: auto; }
</style>
