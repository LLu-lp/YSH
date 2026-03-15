import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  let token = null
  
  // 根据请求路径选择对应的 token
  if (config.url.startsWith('/admin')) {
    token = localStorage.getItem('admin_token')
  } else if (config.url.startsWith('/merchant')) {
    token = localStorage.getItem('merchant_token')
  } else {
    // 用户端接口或公共接口
    token = localStorage.getItem('user_token')
  }
  
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  res => res.data,
  err => {
    ElMessage.error(err.response?.data?.message || '请求失败，请稍后重试')
    return Promise.reject(err)
  }
)

// ====================== 用户端 API ======================

export const userRegister = (data) => request.post('/user/register', data)

export const userLogin = (data) => request.post('/user/login', data)

export const userLogout = () => request.post('/user/logout')

export const getUserInfo = () => request.get('/user/info')

export const updateUserInfo = (data) => request.put('/user/info', data)

export const changePassword = (data) => request.put('/user/password', data)

// ---- 商品 ----
export const getProducts = (params) => request.get('/products', { params })

export const getProductDetail = (id) => request.get(`/products/${id}`)

export const getShopDetail = (id) => request.get(`/shops/${id}`)

export const getShopProducts = (shopId, params) => request.get(`/shops/${shopId}/products`, { params })

export const getShopCoupons = (shopId) => request.get(`/shops/${shopId}/coupons`)

export const followShop = (shopId) => request.post(`/shops/${shopId}/follow`)

export const unfollowShop = (shopId) => request.post(`/shops/${shopId}/unfollow`)

export const isFollowingShop = (shopId) => request.get(`/shops/${shopId}/is-following`)

export const getCategories = () => request.get('/categories')

export const getBanners = () => request.get('/banners')

// ---- 购物车 ----
export const getCartList = () => request.get('/cart')

export const addToCart = (data) => request.post('/cart', data)

export const updateCartItem = (id, data) => request.put(`/cart/${id}`, data)

export const deleteCartItem = (id) => request.delete(`/cart/${id}`)

export const batchDeleteCart = (data) => request.delete('/cart/batch', { data })

// ---- 订单 ----
export const createOrder = (data) => request.post('/orders', data)

export const getUserOrders = (params) => request.get('/orders', { params })

export const getOrderDetail = (id) => request.get(`/orders/${id}`)

export const payOrder = (id, data) => request.post(`/orders/${id}/pay`, data)

export const confirmOrder = (id) => request.post(`/orders/${id}/confirm`)

export const cancelOrder = (id) => request.post(`/orders/${id}/cancel`)

export const applyRefund = (id, data) => request.post(`/orders/${id}/refund`, data)

export const submitReview = (id, data) => request.post(`/orders/${id}/review`, data)

// ---- 收货地址 ----
export const getAddresses = () => request.get('/user/addresses')

export const addAddress = (data) => request.post('/user/addresses', data)

export const updateAddress = (id, data) => request.put(`/user/addresses/${id}`, data)

export const deleteAddress = (id) => request.delete(`/user/addresses/${id}`)

export const setDefaultAddress = (id) => request.put(`/user/addresses/${id}/default`)

// ---- 收藏 ----
export const getFavorites = () => request.get('/user/favorites')

export const addFavorite = (data) => request.post('/user/favorites', data)

export const removeFavorite = (productId) => request.delete(`/user/favorites/${productId}`)

// ---- 优惠券 ----
export const getUserCoupons = (params) => request.get('/user/coupons', { params })

export const receiveCoupon = (data) => request.post('/user/coupons/receive', data)

export const getAvailableCoupons = () => request.get('/user/coupons/available')

// ====================== 商家端 API ======================

export const merchantRegister = (data) => request.post('/merchant/register', data)

export const merchantLogin = (data) => request.post('/merchant/login', data)

export const getShopInfo = () => request.get('/merchant/shop')

export const updateShopInfo = (data) => request.put('/merchant/shop', data)

export const getMerchantProducts = (params) => request.get('/merchant/products', { params })

export const createProduct = (data) => request.post('/merchant/products', data)

export const updateProduct = (id, data) => request.put(`/merchant/products/${id}`, data)

export const deleteProduct = (id) => request.delete(`/merchant/products/${id}`)

export const updateProductStatus = (id, data) => request.put(`/merchant/products/${id}/status`, data)

export const getMerchantOrders = (params) => request.get('/merchant/orders', { params })

export const shipOrder = (id, data) => request.post(`/merchant/orders/${id}/ship`, data)

export const handleRefund = (id, data) => request.post(`/merchant/orders/${id}/refund/handle`, data)

export const getMerchantStats = (params) => request.get('/merchant/stats', { params })

export const getMerchantOverview = () => request.get('/merchant/stats/overview')

export const getMerchantSalesTrend = (params) => request.get('/merchant/stats/sales-trend', { params })

export const getMerchantCategoryRatio = () => request.get('/merchant/stats/category-ratio')

export const getMerchantCoupons = () => request.get('/merchant/coupons')

export const createMerchantCoupon = (data) => request.post('/merchant/coupons', data)

export const deleteMerchantCoupon = (id) => request.delete(`/merchant/coupons/${id}`)

export const getMerchantCustomers = (params) => request.get('/merchant/customers', { params })

// ====================== 管理员端 API ======================

export const adminLogin = (data) => request.post('/admin/login', data)

export const getAdminStats = () => request.get('/admin/stats')

export const getAdminSalesTrend = (params) => request.get('/admin/stats/sales-trend', { params })

export const getAdminTodoList = () => request.get('/admin/stats/todo-list')

export const getAdminPendingCount = () => request.get('/admin/stats/pending-count')

export const adminGetProducts = (params) => request.get('/admin/products', { params })

export const reviewProduct = (id, data) => request.put(`/admin/products/${id}/review`, data)

export const adminDeleteProduct = (id) => request.delete(`/admin/products/${id}`)

export const adminGetMerchants = (params) => request.get('/admin/merchants', { params })

export const reviewMerchant = (id, data) => request.put(`/admin/merchants/${id}/review`, data)

export const freezeMerchant = (id, data) => request.put(`/admin/merchants/${id}/status`, data)

export const getMerchantDetail = (id) => request.get(`/admin/merchants/${id}`)

export const adminGetUsers = (params) => request.get('/admin/users', { params })

export const freezeUser = (id, data) => request.put(`/admin/users/${id}/status`, data)

export const adminGetOrders = (params) => request.get('/admin/orders', { params })

export const adminUpdateOrderStatus = (id, data) => request.put(`/admin/orders/${id}/status`, data)

export const adminHandleRefund = (id, data) => request.post(`/admin/orders/${id}/refund/handle`, data)

export const adminGetCoupons = () => request.get('/admin/coupons')

export const adminCreateCoupon = (data) => request.post('/admin/coupons', data)

export const adminDeleteCoupon = (id) => request.delete(`/admin/coupons/${id}`)

export const adminGetCategories = () => request.get('/admin/categories')

export const adminAddCategory = (data) => request.post('/admin/categories', data)

export const adminUpdateCategory = (id, data) => request.put(`/admin/categories/${id}`, data)

export const adminDeleteCategory = (id) => request.delete(`/admin/categories/${id}`)

// ====================== 数据导出 API ======================

export const exportOrders = (params) => request.get('/admin/export/orders', { params })

export const exportUsers = (params) => request.get('/admin/export/users', { params })

export const exportProducts = (params) => request.get('/admin/export/products', { params })

export const exportMerchants = (params) => request.get('/admin/export/merchants', { params })

// ====================== 系统设置 API ======================

export const getSystemSettings = () => request.get('/admin/settings')

export const saveSystemSettings = (data) => request.put('/admin/settings', data)

export const resetSystemSettings = () => request.post('/admin/settings/reset')

export default request
