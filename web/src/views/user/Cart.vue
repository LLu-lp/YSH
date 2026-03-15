<template>
  <div class="cart-page">
    <div class="page-header">
      <h2 class="page-title">购物车</h2>
      <span class="cart-count">共 {{ cartItems.length }} 件商品</span>
    </div>

    <div v-if="cartItems.length > 0" class="cart-layout">
      <div class="cart-list">
        <div class="cart-header">
          <el-checkbox v-model="allSelected" :indeterminate="isIndeterminate" @change="handleSelectAll">
            全选
          </el-checkbox>
          <span class="col-product">商品</span>
          <span class="col-price">单价</span>
          <span class="col-qty">数量</span>
          <span class="col-total">小计</span>
          <span class="col-action">操作</span>
        </div>

        <div class="cart-item" v-for="item in cartItems" :key="item.id">
          <el-checkbox v-model="item.selected" @change="updateSelectAll" />
          <div class="item-product">
            <img :src="item.productImage || '/placeholder.svg?height=80&width=80'" :alt="item.productName" @click="$router.push(`/user/products/${item.productId}`)" />
            <div class="item-info">
              <p class="item-name" @click="$router.push(`/user/products/${item.productId}`)">{{ item.productName }}</p>
              <p class="item-sku">{{ item.spec || '' }}</p>
            </div>
          </div>
          <div class="col-price">¥{{ item.price }}</div>
          <div class="col-qty">
            <el-input-number v-model="item.quantity" :min="1" :max="item.stock || 99" size="small" @change="handleQtyChange(item)" />
          </div>
          <div class="col-total price-text">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          <div class="col-action">
            <el-button type="danger" link @click="handleDelete(item.id)">删除</el-button>
          </div>
        </div>
      </div>

      <div class="checkout-bar">
        <div class="checkout-left">
          <el-checkbox v-model="allSelected" :indeterminate="isIndeterminate" @change="handleSelectAll">全选</el-checkbox>
          <el-button link type="danger" @click="handleBatchDelete">删除选中</el-button>
        </div>
        <div class="checkout-right">
          <div class="price-summary">
            <span>已选 <strong>{{ selectedCount }}</strong> 件，合计：</span>
            <span class="total-price">¥{{ totalPrice }}</span>
          </div>
          <el-button type="primary" size="large" :disabled="selectedCount === 0" @click="handleCheckout">
            去结算 ({{ selectedCount }})
          </el-button>
        </div>
      </div>
    </div>

    <div v-else class="empty-cart">
      <el-empty description="购物车空空如也，快去选购吧~" :image-size="160">
        <el-button type="primary" @click="$router.push('/user/products')">去购物</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCartList, updateCartItem, deleteCartItem, batchDeleteCart } from '@/api'

const router = useRouter()
const cartItems = ref([])
const allSelected = ref(false)
const isIndeterminate = ref(false)

const selectedCount = computed(() => cartItems.value.filter(i => i.selected).length)
const totalPrice = computed(() =>
  cartItems.value.filter(i => i.selected).reduce((sum, i) => sum + (i.price || 0) * (i.quantity || 0), 0).toFixed(2)
)

const loadCart = async () => {
  try {
    const res = await getCartList()
    if (res.code === 200 && res.data) {
      cartItems.value = (Array.isArray(res.data) ? res.data : res.data.content || []).map(item => ({
        ...item,
        selected: item.checked !== undefined ? item.checked === 1 : true,
        productName: item.productName || item.name,
        productImage: item.productImage || item.mainImage || item.image
      }))
      updateSelectAll()
    }
  } catch (error) {
    console.error('加载购物车失败:', error)
  }
}

const updateSelectAll = () => {
  const selected = cartItems.value.filter(i => i.selected).length
  allSelected.value = selected === cartItems.value.length && cartItems.value.length > 0
  isIndeterminate.value = selected > 0 && selected < cartItems.value.length
}

const handleSelectAll = (val) => {
  cartItems.value.forEach(i => i.selected = val)
  isIndeterminate.value = false
}

const handleQtyChange = async (item) => {
  try {
    await updateCartItem(item.id, { quantity: item.quantity })
  } catch (error) {
    console.error('更新数量失败:', error)
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' })
  try {
    await deleteCartItem(id)
    cartItems.value = cartItems.value.filter(i => i.id !== id)
    ElMessage.success('已删除')
    updateSelectAll()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const handleBatchDelete = async () => {
  const ids = cartItems.value.filter(i => i.selected).map(i => i.id)
  if (!ids.length) return ElMessage.warning('请先选中商品')
  await ElMessageBox.confirm(`确定删除选中的 ${ids.length} 件商品？`, '提示', { type: 'warning' })
  try {
    await batchDeleteCart({ ids })
    cartItems.value = cartItems.value.filter(i => !ids.includes(i.id))
    ElMessage.success('已删除')
    updateSelectAll()
  } catch (error) {
    console.error('批量删除失败:', error)
  }
}

const handleCheckout = () => {
  if (!selectedCount.value) return
  const selectedItems = cartItems.value.filter(i => i.selected)
  router.push({ path: '/user/checkout', query: { action: 'cart', items: JSON.stringify(selectedItems.map(i => i.id)) } })
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-page { background:#fff; border-radius:12px; padding:24px; }
.page-header { display:flex; align-items:center; gap:12px; margin-bottom:24px; }
.page-title { font-size:22px; font-weight:700; color:#333; }
.cart-count { font-size:14px; color:#999; }
.cart-header { display:flex; align-items:center; gap:0; background:#f8f8f8; padding:12px 16px; border-radius:8px; margin-bottom:8px; font-size:13px; color:#666; }
.col-product { flex:1; margin-left:12px; }
.col-price, .col-qty, .col-total, .col-action { width:120px; text-align:center; }
.cart-item { display:flex; align-items:center; padding:16px; border:1px solid #f5f5f5; border-radius:8px; margin-bottom:8px; }
.item-product { flex:1; display:flex; align-items:center; gap:12px; margin-left:12px; }
.item-product img { width:80px; height:80px; object-fit:cover; border-radius:6px; cursor:pointer; flex-shrink:0; }
.item-name { font-size:14px; color:#333; cursor:pointer; margin-bottom:4px; }
.item-name:hover { color:#ff6b35; }
.item-sku { font-size:12px; color:#999; }
.col-price, .col-qty, .col-total, .col-action { width:120px; text-align:center; }
.price-text { font-size:16px; font-weight:600; color:#ff6b35; }
.checkout-bar { position:sticky; bottom:0; background:#fff; border:1px solid #f0f0f0; border-radius:8px; padding:16px 24px; display:flex; align-items:center; justify-content:space-between; margin-top:16px; box-shadow:0 -2px 10px rgba(0,0,0,0.06); }
.checkout-left { display:flex; align-items:center; gap:16px; }
.checkout-right { display:flex; align-items:center; gap:24px; }
.price-summary { font-size:14px; color:#666; }
.total-price { font-size:24px; font-weight:700; color:#ff6b35; }
.empty-cart { padding:80px 0; }
</style>
