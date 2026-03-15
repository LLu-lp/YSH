<template>
  <div class="admin-layout">
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="brand">
          <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="32" height="32" rx="8" fill="#1c1917"/>
            <path d="M8 22l3-10h10l3 10H8z" fill="white" opacity="0.9"/>
            <circle cx="16" cy="11" r="3" fill="white"/>
          </svg>
          <span v-if="!sidebarCollapsed" class="brand-name">后台管理</span>
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
          <p v-if="!sidebarCollapsed" class="nav-label">核心模块</p>
          <router-link to="/admin/dashboard" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
            <span v-if="!sidebarCollapsed">数据看板</span>
          </router-link>
          <router-link to="/admin/products" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/></svg>
            <span v-if="!sidebarCollapsed">商品审核</span>
            <span v-if="!sidebarCollapsed && pendingAuditCount > 0" class="badge">{{ pendingAuditCount }}</span>
          </router-link>
          <router-link to="/admin/merchants" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>
            <span v-if="!sidebarCollapsed">商家管理</span>
          </router-link>
          <router-link to="/admin/users" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>
            <span v-if="!sidebarCollapsed">用户管理</span>
          </router-link>
          <router-link to="/admin/orders" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            <span v-if="!sidebarCollapsed">订单管理</span>
          </router-link>
          <router-link to="/admin/categories" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 6h16M4 10h16M4 14h16M4 18h16"/></svg>
            <span v-if="!sidebarCollapsed">分类管理</span>
          </router-link>
          <router-link to="/admin/coupons" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16v2H4V4zm0 6h16v2H4v-2zm0 6h16v2H4v-2z"/><rect x="6" y="10" width="2" height="2"/><rect x="16" y="10" width="2" height="2"/></svg>
            <span v-if="!sidebarCollapsed">优惠券管理</span>
          </router-link>
          <router-link to="/admin/export" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
            <span v-if="!sidebarCollapsed">数据导出</span>
          </router-link>
          <router-link to="/admin/settings" class="nav-item" active-class="active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M12 1v6m0 6v6M4.22 4.22l4.24 4.24m5.08 5.08l4.24 4.24M1 12h6m6 0h6M4.22 19.78l4.24-4.24m5.08-5.08l4.24-4.24"/></svg>
            <span v-if="!sidebarCollapsed">系统设置</span>
          </router-link>
        </div>
      </nav>
      <div class="sidebar-footer">
        <div class="admin-info" v-if="!sidebarCollapsed">
          <div class="admin-avatar">A</div>
          <div>
            <p class="admin-name">{{ adminStore.admin?.username || '管理员' }}</p>
            <p class="admin-role">超级管理员</p>
          </div>
        </div>
        <button class="logout-btn" @click="handleLogout" :title="sidebarCollapsed ? '退出登录' : ''">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
          <span v-if="!sidebarCollapsed">退出登录</span>
        </button>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/user'
import { getAdminPendingCount } from '@/api'

const router = useRouter()
const adminStore = useAdminStore()
const sidebarCollapsed = ref(false)
const pendingAuditCount = ref(0)

const loadPendingCount = async () => {
  try {
    const res = await getAdminPendingCount()
    if (res.code === 200 && res.data) {
      pendingAuditCount.value = res.data.pendingProductAudits || 0
    }
  } catch (error) {
    console.error('加载待审核数量失败:', error)
  }
}

const handleLogout = () => {
  adminStore.logout()
  router.push('/admin/login')
}

onMounted(() => {
  loadPendingCount()
})
</script>

<style scoped>
.admin-layout { display: flex; min-height: 100vh; background: #f5f5f4; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.sidebar { width: 220px; background: #1c1917; display: flex; flex-direction: column; transition: width 0.25s ease; flex-shrink: 0; }
.sidebar.collapsed { width: 64px; }
.sidebar-header { padding: 16px 12px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #292524; }
.brand { display: flex; align-items: center; gap: 10px; overflow: hidden; }
.brand svg { width: 32px; height: 32px; flex-shrink: 0; }
.brand-name { font-size: 15px; font-weight: 700; color: white; white-space: nowrap; }
.collapse-btn { background: none; border: none; cursor: pointer; color: #78716c; padding: 4px; border-radius: 6px; display: flex; }
.collapse-btn:hover { color: white; }
.collapse-btn svg { width: 18px; height: 18px; }
.sidebar-nav { flex: 1; padding: 16px 8px; overflow-y: auto; }
.nav-label { font-size: 11px; color: #57534e; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; padding: 0 8px; margin: 0 0 6px; }
.nav-item { display: flex; align-items: center; gap: 10px; padding: 9px 10px; border-radius: 8px; color: #a8a29e; text-decoration: none; font-size: 14px; font-weight: 500; transition: all 0.15s; margin-bottom: 2px; }
.nav-item svg { width: 18px; height: 18px; flex-shrink: 0; }
.nav-item:hover { background: #292524; color: white; }
.nav-item.active { background: #292524; color: white; font-weight: 600; }
.badge { margin-left: auto; background: #f97316; color: white; font-size: 11px; padding: 1px 6px; border-radius: 10px; font-weight: 700; }
.sidebar-footer { padding: 12px 8px; border-top: 1px solid #292524; }
.admin-info { display: flex; align-items: center; gap: 10px; padding: 8px 10px; margin-bottom: 4px; }
.admin-avatar { width: 32px; height: 32px; background: #f97316; border-radius: 8px; color: white; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 700; flex-shrink: 0; }
.admin-name { font-size: 13px; font-weight: 600; color: white; margin: 0; }
.admin-role { font-size: 11px; color: #57534e; margin: 0; }
.logout-btn { display: flex; align-items: center; gap: 10px; width: 100%; padding: 9px 10px; background: none; border: none; border-radius: 8px; color: #a8a29e; font-size: 14px; cursor: pointer; transition: all 0.15s; }
.logout-btn svg { width: 18px; height: 18px; flex-shrink: 0; }
.logout-btn:hover { background: #292524; color: #ef4444; }
.main-content { flex: 1; overflow: auto; }
</style>
