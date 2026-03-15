<template>
  <div class="products-page">
    <div class="page-header">
      <h2>商品管理</h2>
      <button class="btn-add" @click="openModal('add')">+ 添加商品</button>
    </div>

    <div class="filter-bar">
      <select v-model="filters.categoryId">
        <option value="">全部分类</option>
        <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
      </select>
      <select v-model="filters.status">
        <option value="">全部状态</option>
        <option value="1">在售</option>
        <option value="0">下架</option>
      </select>
      <div class="search-wrap">
        <input v-model="filters.keyword" type="text" placeholder="搜索商品名称..." @keyup.enter="loadProducts" />
        <button @click="loadProducts">搜索</button>
      </div>
    </div>

    <div class="table-card" v-loading="loading">
      <table class="data-table">
        <thead>
          <tr>
            <th>商品图片</th>
            <th>商品名称</th>
            <th>分类</th>
            <th>价格</th>
            <th>库存</th>
            <th>销量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>
              <img :src="product.mainImage || '/placeholder.svg?height=60&width=60'" class="product-img" alt="商品图片" />
            </td>
            <td>
              <p class="product-name">{{ product.name }}</p>
              <p class="product-no">ID: {{ product.id }}</p>
            </td>
            <td>{{ product.categoryName || '未分类' }}</td>
            <td class="price">¥{{ product.price }}</td>
            <td :class="{ 'stock-low': product.stock < 10 }">{{ product.stock }}</td>
            <td>{{ product.salesCount || 0 }}</td>
            <td>
              <span class="status-tag" :class="getStatusClass(product.status, product.auditStatus)">{{ getStatusText(product.status, product.auditStatus) }}</span>
            </td>
            <td>
              <div class="action-btns">
                <button class="btn-sm edit" @click="openModal('edit', product)">编辑</button>
                <button class="btn-sm" :class="product.status === 1 ? 'off' : 'on'" @click="toggleStatus(product)">
                  {{ product.status === 1 ? '下架' : '上架' }}
                </button>
                <button class="btn-sm danger" @click="deleteProductItem(product)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="products.length === 0 && !loading">
            <td colspan="8" style="text-align: center; color: #a8a29e; padding: 40px;">暂无商品数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <button @click="page > 1 && (page--, loadProducts())" :disabled="page === 1">上一页</button>
        <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
        <button @click="page < totalPages && (page++, loadProducts())" :disabled="page === totalPages">下一页</button>
      </div>
    </div>

    <div v-if="showModal" class="modal-mask" @click.self="showModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ modalMode === 'add' ? '添加商品' : '编辑商品' }}</h3>
          <button @click="showModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-grid">
            <div class="form-item full">
              <label>商品名称 <span class="required">*</span></label>
              <input v-model="form.name" type="text" placeholder="请输入商品名称" />
            </div>
            <div class="form-item">
              <label>商品分类 <span class="required">*</span></label>
              <select v-model="form.categoryId">
                <option value="">请选择分类</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
              </select>
            </div>
            <div class="form-item">
              <label>售价（元）<span class="required">*</span></label>
              <input v-model="form.price" type="number" step="0.01" min="0" placeholder="请输入售价" />
            </div>
            <div class="form-item">
              <label>原价（元）</label>
              <input v-model="form.originalPrice" type="number" step="0.01" min="0" placeholder="划线价（选填）" />
            </div>
            <div class="form-item">
              <label>库存数量 <span class="required">*</span></label>
              <input v-model="form.stock" type="number" min="0" placeholder="请输入库存" />
            </div>
            <div class="form-item full">
              <label>商品封面图</label>
              <input v-model="form.mainImage" type="text" placeholder="请输入图片URL" />
            </div>
            <div class="form-item full">
              <label>商品描述</label>
              <textarea v-model="form.description" rows="4" placeholder="请输入商品描述..."></textarea>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showModal = false">取消</button>
          <button class="btn-confirm" @click="handleSave" :disabled="saving">{{ saving ? '提交中...' : '提交审核' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMerchantProducts, createProduct, updateProduct, deleteProduct, updateProductStatus, getCategories } from '@/api'

const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)
const saving = ref(false)
const showModal = ref(false)
const modalMode = ref('add')

const filters = ref({ categoryId: '', status: '', keyword: '' })
const categories = ref([])
const products = ref([])

const form = ref({ name: '', categoryId: '', price: '', originalPrice: '', stock: '', mainImage: '', description: '' })

const getStatusClass = (status, auditStatus) => {
  if (auditStatus === 0) return 'pending'
  if (auditStatus === 2) return 'danger'
  return status === 1 ? 'success' : 'off'
}

const getStatusText = (status, auditStatus) => {
  if (auditStatus === 0) return '审核中'
  if (auditStatus === 2) return '已拒绝'
  return status === 1 ? '在售中' : '已下架'
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
      pageSize: pageSize,
      categoryId: filters.value.categoryId || undefined,
      status: filters.value.status || undefined,
      keyword: filters.value.keyword || undefined
    }
    const res = await getMerchantProducts(params)
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

const openModal = (mode, product = null) => {
  modalMode.value = mode
  if (mode === 'edit' && product) {
    form.value = {
      name: product.name,
      categoryId: product.categoryId,
      price: product.price,
      originalPrice: product.originalPrice,
      stock: product.stock,
      mainImage: product.mainImage,
      description: product.description
    }
    editId.value = product.id
  } else {
    form.value = { name: '', categoryId: '', price: '', originalPrice: '', stock: '', mainImage: '', description: '' }
    editId.value = null
  }
  showModal.value = true
}

const editId = ref(null)

const handleSave = async () => {
  if (!form.value.name || !form.value.categoryId || !form.value.price || !form.value.stock) {
    ElMessage.warning('请填写必填项')
    return
  }
  saving.value = true
  try {
    const data = {
      name: form.value.name,
      categoryId: form.value.categoryId,
      price: parseFloat(form.value.price),
      originalPrice: form.value.originalPrice ? parseFloat(form.value.originalPrice) : null,
      stock: parseInt(form.value.stock),
      mainImage: form.value.mainImage,
      description: form.value.description
    }
    if (modalMode.value === 'add') {
      await createProduct(data)
      ElMessage.success('商品已提交审核')
    } else {
      await updateProduct(editId.value, data)
      ElMessage.success('修改成功')
    }
    showModal.value = false
    loadProducts()
  } catch (error) {
    console.error('保存商品失败:', error)
  } finally {
    saving.value = false
  }
}

const toggleStatus = async (product) => {
  try {
    const newStatus = product.status === 1 ? 0 : 1
    await updateProductStatus(product.id, { status: newStatus })
    product.status = newStatus
    ElMessage.success(newStatus === 1 ? '商品已上架' : '商品已下架')
  } catch (error) {
    console.error('更新状态失败:', error)
  }
}

const deleteProductItem = async (product) => {
  if (!confirm(`确认删除商品「${product.name}」吗？`)) return
  try {
    await deleteProduct(product.id)
    products.value = products.value.filter(p => p.id !== product.id)
    ElMessage.success('删除成功')
  } catch (error) {
    console.error('删除商品失败:', error)
  }
}

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>

<style scoped>
.products-page { padding: 28px 32px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0; }
.btn-add { padding: 10px 20px; background: #f97316; color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.filter-bar select, .filter-bar input[type="text"] { padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; color: #44403c; outline: none; background: white; }
.search-wrap { display: flex; gap: 0; }
.search-wrap input { border-radius: 8px 0 0 8px; width: 200px; }
.search-wrap button { padding: 9px 16px; background: #f97316; color: white; border: none; border-radius: 0 8px 8px 0; font-size: 13px; cursor: pointer; }
.table-card { background: white; border-radius: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th { text-align: left; padding: 14px 16px; color: #a8a29e; font-weight: 500; background: #fafaf9; border-bottom: 1px solid #f5f5f4; }
.data-table td { padding: 12px 16px; color: #44403c; border-bottom: 1px solid #f5f5f4; vertical-align: middle; }
.product-img { width: 52px; height: 52px; border-radius: 8px; object-fit: cover; background: #f5f5f4; }
.product-name { font-size: 13px; font-weight: 500; color: #1c1917; margin: 0 0 3px; max-width: 200px; }
.product-no { font-size: 11px; color: #a8a29e; margin: 0; }
.price { font-weight: 600; color: #f97316; }
.stock-low { color: #ef4444; font-weight: 600; }
.status-tag { padding: 3px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.status-tag.success { background: #f0fdf4; color: #10b981; }
.status-tag.off { background: #f5f5f4; color: #a8a29e; }
.status-tag.pending { background: #fefce8; color: #ca8a04; }
.status-tag.danger { background: #fef2f2; color: #ef4444; }
.action-btns { display: flex; gap: 6px; }
.btn-sm { padding: 5px 10px; border-radius: 6px; font-size: 12px; cursor: pointer; border: 1.5px solid; }
.btn-sm.edit { border-color: #dbeafe; color: #3b82f6; background: #eff6ff; }
.btn-sm.on { border-color: #d1fae5; color: #10b981; background: #f0fdf4; }
.btn-sm.off { border-color: #e7e5e4; color: #78716c; background: #fafaf9; }
.btn-sm.danger { border-color: #fecaca; color: #ef4444; background: #fef2f2; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 12px; padding: 14px 16px; border-top: 1px solid #f5f5f4; font-size: 13px; color: #78716c; }
.pagination button { padding: 6px 14px; border: 1.5px solid #e7e5e4; border-radius: 6px; background: white; font-size: 13px; cursor: pointer; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: white; border-radius: 14px; width: 600px; max-height: 80vh; display: flex; flex-direction: column; overflow: hidden; }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; border-bottom: 1px solid #f5f5f4; }
.modal-header h3 { font-size: 16px; font-weight: 600; color: #1c1917; margin: 0; }
.modal-close { background: none; border: none; font-size: 22px; color: #a8a29e; cursor: pointer; }
.modal-body { flex: 1; overflow-y: auto; padding: 20px 24px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.form-item { display: flex; flex-direction: column; gap: 6px; }
.form-item.full { grid-column: 1 / -1; }
.form-item label { font-size: 13px; color: #78716c; font-weight: 500; }
.required { color: #ef4444; }
.form-item input, .form-item select, .form-item textarea { padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; color: #1c1917; outline: none; font-family: inherit; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 24px; border-top: 1px solid #f5f5f4; }
.btn-cancel { padding: 9px 20px; background: white; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; cursor: pointer; }
.btn-confirm { padding: 9px 20px; background: #f97316; color: white; border: none; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; }
.btn-confirm:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
