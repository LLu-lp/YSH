import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 入口页
  { path: '/', component: () => import('@/views/Home.vue') },

  // ====== 用户端 ======
  { path: '/user/login', component: () => import('@/views/user/Login.vue'), meta: { title: '用户登录' } },
  { path: '/user/register', component: () => import('@/views/user/Register.vue'), meta: { title: '用户注册' } },
  {
    path: '/user',
    component: () => import('@/views/user/UserLayout.vue'),
    children: [
      { path: '', redirect: '/user/home' },
      { path: 'home', component: () => import('@/views/user/UserHome.vue'), meta: { title: '首页' } },
      { path: 'products', component: () => import('@/views/user/ProductList.vue'), meta: { title: '商品列表' } },
      { path: 'products/:id', component: () => import('@/views/user/ProductDetail.vue'), meta: { title: '商品详情' } },
      { path: 'shop/:id', component: () => import('@/views/user/ShopDetail.vue'), meta: { title: '店铺主页' } },
      { path: 'cart', component: () => import('@/views/user/Cart.vue'), meta: { title: '购物车', requiresUserAuth: true } },
      { path: 'checkout', component: () => import('@/views/user/Checkout.vue'), meta: { title: '确认订单', requiresUserAuth: true } },
      { path: 'orders', component: () => import('@/views/user/Orders.vue'), meta: { title: '我的订单', requiresUserAuth: true } },
      { path: 'orders/:id', component: () => import('@/views/user/OrderDetail.vue'), meta: { title: '订单详情', requiresUserAuth: true } },
      { path: 'profile', component: () => import('@/views/user/Profile.vue'), meta: { title: '个人信息', requiresUserAuth: true } },
      { path: 'address', component: () => import('@/views/user/Address.vue'), meta: { title: '收货地址', requiresUserAuth: true } },
      { path: 'favorites', component: () => import('@/views/user/Favorites.vue'), meta: { title: '我的收藏', requiresUserAuth: true } },
      { path: 'coupons', component: () => import('@/views/user/Coupons.vue'), meta: { title: '我的优惠券', requiresUserAuth: true } },
    ]
  },

  // ====== 商家端 ======
  { path: '/merchant/login', component: () => import('@/views/merchant/MerchantLogin.vue'), meta: { title: '商家登录' } },
  { path: '/merchant/register', component: () => import('@/views/merchant/MerchantRegister.vue'), meta: { title: '商家入驻' } },
  {
    path: '/merchant',
    component: () => import('@/views/merchant/MerchantLayout.vue'),
    meta: { requiresMerchantAuth: true },
    children: [
      { path: '', redirect: '/merchant/dashboard' },
      { path: 'dashboard', component: () => import('@/views/merchant/MerchantDashboard.vue'), meta: { title: '数据概览' } },
      { path: 'shop', component: () => import('@/views/merchant/ShopManage.vue'), meta: { title: '店铺管理' } },
      { path: 'products', component: () => import('@/views/merchant/ProductManage.vue'), meta: { title: '商品管理' } },
      { path: 'orders', component: () => import('@/views/merchant/OrderManage.vue'), meta: { title: '订单管理' } },
      { path: 'coupons', component: () => import('@/views/merchant/CouponManage.vue'), meta: { title: '优惠券管理' } },
      { path: 'customers', component: () => import('@/views/merchant/CustomerManage.vue'), meta: { title: '用户管理' } },
    ]
  },

  // ====== 管理员端 ======
  { path: '/admin/login', component: () => import('@/views/admin/AdminLogin.vue'), meta: { title: '管理员登录' } },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAdminAuth: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: () => import('@/views/admin/AdminDashboard.vue'), meta: { title: '数据看板' } },
      { path: 'products', component: () => import('@/views/admin/ProductAudit.vue'), meta: { title: '商品审核' } },
      { path: 'categories', component: () => import('@/views/admin/CategoryManage.vue'), meta: { title: '分类管理' } },
      { path: 'merchants', component: () => import('@/views/admin/MerchantManage.vue'), meta: { title: '商家管理' } },
      { path: 'users', component: () => import('@/views/admin/UserManage.vue'), meta: { title: '用户管理' } },
      { path: 'orders', component: () => import('@/views/admin/AdminOrderManage.vue'), meta: { title: '订单管理' } },
      { path: 'coupons', component: () => import('@/views/admin/CouponManage.vue'), meta: { title: '优惠券管理' } },
      { path: 'export', component: () => import('@/views/admin/DataExport.vue'), meta: { title: '数据导出' } },
      { path: 'settings', component: () => import('@/views/admin/SystemSettings.vue'), meta: { title: '系统设置' } },
    ]
  },

  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 好物购买系统` : '好物购买系统'

  const userToken = localStorage.getItem('user_token')
  const merchantToken = localStorage.getItem('merchant_token')
  const adminToken = localStorage.getItem('admin_token')

  // 检查当前路由是否需要认证
  if (to.meta.requiresUserAuth && !userToken) {
    next('/user/login')
  } else if (to.meta.requiresMerchantAuth && !merchantToken) {
    next('/merchant/login')
  } else if (to.meta.requiresAdminAuth && !adminToken) {
    next('/admin/login')
  } else {
    next()
  }
})

export default router
