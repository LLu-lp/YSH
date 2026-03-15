import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 用户端 Store
export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('user_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('user_info') || 'null'))
  const cartCount = ref(0)

  const isLoggedIn = computed(() => !!token.value)

  function setUser(tokenVal, info) {
    token.value = tokenVal
    userInfo.value = info
    localStorage.setItem('user_token', tokenVal)
    localStorage.setItem('user_info', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('user_token')
    localStorage.removeItem('user_info')
  }

  function setCartCount(count) {
    cartCount.value = count
  }

  return { token, userInfo, cartCount, isLoggedIn, setUser, logout, setCartCount }
})

// 商家端 Store
export const useMerchantStore = defineStore('merchant', () => {
  const token = ref(localStorage.getItem('merchant_token') || '')
  const merchantInfo = ref(JSON.parse(localStorage.getItem('merchant_info') || 'null'))

  const isLoggedIn = computed(() => !!token.value)

  function setMerchant(tokenVal, info) {
    token.value = tokenVal
    merchantInfo.value = info
    localStorage.setItem('merchant_token', tokenVal)
    localStorage.setItem('merchant_info', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    merchantInfo.value = null
    localStorage.removeItem('merchant_token')
    localStorage.removeItem('merchant_info')
  }

  return { token, merchantInfo, isLoggedIn, setMerchant, logout }
})

// 管理员 Store
export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const adminInfo = ref(JSON.parse(localStorage.getItem('admin_info') || 'null'))

  const isLoggedIn = computed(() => !!token.value)

  function setAdmin(tokenVal, info) {
    token.value = tokenVal
    adminInfo.value = info
    localStorage.setItem('admin_token', tokenVal)
    localStorage.setItem('admin_info', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    adminInfo.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
  }

  return { token, adminInfo, isLoggedIn, setAdmin, logout }
})
