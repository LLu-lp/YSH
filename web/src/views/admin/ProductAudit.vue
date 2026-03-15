<template>
  <div class="audit-page">
    <div class="page-header">
      <h2>商品审核</h2>
    </div>

    <div class="status-tabs">
      <button v-for="tab in auditTabs" :key="tab.value" class="tab-btn" :class="{ active: activeAuditTab === tab.value }" @click="switchAuditTab(tab.value)">
        {{ tab.label }}<span v-if="tab.count" class="tab-count">{{ tab.count }}</span>
      </button>
    </div>

    <div class="filter-bar">
      <select v-model="filters.categoryId">
        <option value="">全部分类</option>
        <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
      </select>
      <input v-model="filters.keyword" type="text" placeholder="搜索商品名称 / 商家名称..." @keyup.enter="loadList" />
      <button class="btn-search" @click="loadList">搜索</button>
    </div>

    <div class="table-card" v-loading="loading">
      <table class="data-table">
        <thead>
          <tr>
            <th>商品信息</th>
            <th>分类</th>
            <th>所属商家</th>
            <th>价格</th>
            <th>库存</th>
            <th>提交时间</th>
            <th>审核状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>
              <div class="product-cell">
                <img :src="product.mainImage || '/placeholder.svg?height=52&width=52'" class="product-img" alt="商品图片" />
                <div>
                  <p class="product-name">{{ product.name }}</p>
                  <p class="product-id">ID: {{ product.id }}</p>
                </div>
              </div>
            </td>
            <td>{{ product.categoryName || '未分类' }}</td>
            <td>{{ product.shopName || '-' }}</td>
            <td class="price">¥{{ product.price }}</td>
            <td>{{ product.stock }}</td>
            <td class="time">{{ formatDateTime(product.createTime) }}</td>
            <td>
              <span class="status-tag" :class="statusMap[product.auditStatus]?.cls">{{ statusMap[product.auditStatus]?.text }}</span>
            </td>
            <td>
              <div class="action-btns">
                <button class="btn-sm view" @click="openDetail(product)">详情</button>
                <button class="btn-sm approve" @click="handleApprove(product)" v-if="product.auditStatus === 0">通过</button>
                <button class="btn-sm reject" @click="handleReject(product)" v-if="product.auditStatus === 0">拒绝</button>
              </div>
            </td>
          </tr>
          <tr v-if="products.length === 0 && !loading">
            <td colspan="8" style="text-align: center; color: #a8a29e; padding: 40px;">暂无商品数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <button @click="page > 1 && (page--, loadList())" :disabled="page === 1">上一页</button>
        <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
        <button @click="page < totalPages && (page++, loadList())" :disabled="page === totalPages">下一页</button>
      </div>
    </div>

    <div v-if="showDetail && currentProduct" class="modal-mask" @click.self="showDetail = false">
      <div class="modal">
        <div class="modal-header">
          <h3>商品详情</h3>
          <button @click="showDetail = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <img :src="currentProduct.mainImage || '/placeholder.svg?height=200&width=400'" class="detail-img" alt="商品封面" />
          <table class="detail-table">
            <tr><td class="dt-label">商品名称</td><td>{{ currentProduct.name }}</td></tr>
            <tr><td class="dt-label">所属分类</td><td>{{ currentProduct.categoryName || '未分类' }}</td></tr>
            <tr><td class="dt-label">所属商家</td><td>{{ currentProduct.shopName || '-' }}</td></tr>
            <tr><td class="dt-label">售价</td><td class="price">¥{{ currentProduct.price }}</td></tr>
            <tr><td class="dt-label">库存</td><td>{{ currentProduct.stock }}</td></tr>
            <tr><td class="dt-label">商品描述</td><td>{{ currentProduct.description || '暂无描述' }}</td></tr>
            <tr><td class="dt-label">提交时间</td><td>{{ formatDateTime(currentProduct.createTime) }}</td></tr>
          </table>
        </div>
        <div class="modal-footer" v-if="currentProduct.auditStatus === 0">
          <button class="btn-reject" @click="handleReject(currentProduct); showDetail = false">拒绝</button>
          <button class="btn-approve" @click="handleApprove(currentProduct); showDetail = false">通过审核</button>
        </div>
      </div>
    </div>

    <div v-if="showRejectModal" class="modal-mask" @click.self="showRejectModal = false">
      <div class="modal sm">
        <div class="modal-header">
          <h3>填写拒绝原因</h3>
          <button @click="showRejectModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>拒绝原因 <span class="required">*</span></label>
            <textarea v-model="rejectReason" rows="4" placeholder="请填写具体的拒绝原因，将通知商家修改..."></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showRejectModal = false">取消</button>
          <button class="btn-confirm-reject" @click="confirmReject">确认拒绝</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGetProducts, reviewProduct, getCategories } from '@/api'

const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)
const showDetail = ref(false)
const showRejectModal = ref(false)
const currentProduct = ref(null)
const rejectReason = ref('')

const activeAuditTab = ref(0)
const auditTabs = ref([
  { label: '全部商品', value: null, count: 0 },
  { label: '待审核', value: 0, count: 0 },
  { label: '已通过', value: 1, count: 0 },
  { label: '已拒绝', value: 2, count: 0 }
])

const filters = ref({ categoryId: '', keyword: '' })
const categories = ref([])

const statusMap = {
  0: { text: '待审核', cls: 'pending' },
  1: { text: '已通过', cls: 'success' },
  2: { text: '已拒绝', cls: 'danger' }
}

const products = ref([])

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
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

const switchAuditTab = (val) => {
  activeAuditTab.value = val
  page.value = 1
  loadList()
}

const loadList = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize,
      auditStatus: activeAuditTab.value !== null ? activeAuditTab.value : undefined,
      categoryId: filters.value.categoryId || undefined,
      keyword: filters.value.keyword || undefined
    }
    const res = await adminGetProducts(params)
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
    console.error('加载商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const openDetail = (product) => {
  currentProduct.value = product
  showDetail.value = true
}

const handleApprove = async (product) => {
  if (!confirm(`确认通过商品「${product.name}」的审核？`)) return
  try {
    await reviewProduct(product.id, { auditStatus: 1 })
    product.auditStatus = 1
    ElMessage.success('审核通过')
    loadList()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

const handleReject = (product) => {
  currentProduct.value = product
  rejectReason.value = ''
  showRejectModal.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写拒绝原因')
    return
  }
  try {
    await reviewProduct(currentProduct.value.id, { auditStatus: 2, auditRemark: rejectReason.value })
    currentProduct.value.auditStatus = 2
    showRejectModal.value = false
    ElMessage.success('已拒绝')
    loadList()
  } catch (error) {
    console.error('拒绝失败:', error)
    ElMessage.error('拒绝失败')
  }
}

onMounted(() => {
  loadCategories()
  loadList()
})
</script>

<style scoped>
.audit-page { padding: 28px 32px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0; }
.status-tabs { display: flex; gap: 4px; margin-bottom: 16px; border-bottom: 1px solid #e7e5e4; }
.tab-btn { padding: 10px 16px; background: none; border: none; font-size: 13px; color: #78716c; cursor: pointer; border-bottom: 2px solid transparent; margin-bottom: -1px; display: flex; align-items: center; gap: 6px; }
.tab-btn:hover { color: #1c1917; }
.tab-btn.active { color: #1c1917; border-bottom-color: #1c1917; font-weight: 600; }
.tab-count { background: #ef4444; color: white; font-size: 11px; padding: 1px 6px; border-radius: 10px; font-weight: 700; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.filter-bar select, .filter-bar input { padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; color: #44403c; outline: none; background: white; }
.filter-bar input { min-width: 240px; }
.btn-search { padding: 9px 18px; background: #1c1917; color: white; border: none; border-radius: 8px; font-size: 13px; cursor: pointer; }
.table-card { background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th { text-align: left; padding: 14px 16px; color: #a8a29e; font-weight: 500; background: #fafaf9; border-bottom: 1px solid #f5f5f4; }
.data-table td { padding: 12px 16px; color: #44403c; border-bottom: 1px solid #f5f5f4; vertical-align: middle; }
.product-cell { display: flex; align-items: center; gap: 10px; }
.product-img { width: 52px; height: 52px; border-radius: 8px; object-fit: cover; background: #f5f5f4; flex-shrink: 0; }
.product-name { font-size: 13px; font-weight: 500; color: #1c1917; margin: 0 0 3px; max-width: 200px; }
.product-id { font-size: 11px; color: #a8a29e; margin: 0; }
.price { font-weight: 600; color: #f97316; }
.time { color: #a8a29e; font-size: 12px; }
.status-tag { padding: 3px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.status-tag.pending { background: #fefce8; color: #ca8a04; }
.status-tag.success { background: #f0fdf4; color: #10b981; }
.status-tag.danger { background: #fef2f2; color: #ef4444; }
.action-btns { display: flex; gap: 6px; }
.btn-sm { padding: 5px 10px; border-radius: 6px; font-size: 12px; cursor: pointer; border: 1.5px solid; font-weight: 500; }
.btn-sm.view { border-color: #e7e5e4; color: #78716c; background: #fafaf9; }
.btn-sm.approve { border-color: #d1fae5; color: #10b981; background: #f0fdf4; }
.btn-sm.approve:hover { background: #10b981; color: white; }
.btn-sm.reject { border-color: #fecaca; color: #ef4444; background: #fef2f2; }
.btn-sm.reject:hover { background: #ef4444; color: white; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 12px; padding: 14px 16px; border-top: 1px solid #f5f5f4; font-size: 13px; color: #78716c; }
.pagination button { padding: 6px 14px; border: 1.5px solid #e7e5e4; border-radius: 6px; background: white; font-size: 13px; cursor: pointer; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: white; border-radius: 14px; width: 560px; max-height: 80vh; display: flex; flex-direction: column; overflow: hidden; }
.modal.sm { width: 440px; }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 18px 22px; border-bottom: 1px solid #f5f5f4; }
.modal-header h3 { font-size: 16px; font-weight: 600; color: #1c1917; margin: 0; }
.modal-close { background: none; border: none; font-size: 22px; color: #a8a29e; cursor: pointer; }
.modal-body { flex: 1; overflow-y: auto; padding: 20px 22px; }
.detail-img { width: 100%; height: 200px; object-fit: cover; border-radius: 10px; margin-bottom: 16px; background: #f5f5f4; }
.detail-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.detail-table tr { border-bottom: 1px solid #f5f5f4; }
.detail-table td { padding: 10px 0; color: #44403c; vertical-align: top; }
.dt-label { color: #a8a29e; width: 80px; font-weight: 500; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 14px 22px; border-top: 1px solid #f5f5f4; }
.btn-reject { padding: 9px 20px; background: #fef2f2; color: #ef4444; border: 1.5px solid #fecaca; border-radius: 8px; font-size: 13px; cursor: pointer; font-weight: 500; }
.btn-approve { padding: 9px 20px; background: #1c1917; color: white; border: none; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; }
.form-item { display: flex; flex-direction: column; gap: 6px; }
.form-item label { font-size: 13px; color: #78716c; font-weight: 500; }
.required { color: #ef4444; }
.form-item textarea { padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; resize: vertical; outline: none; font-family: inherit; color: #1c1917; }
.btn-cancel { padding: 9px 20px; background: white; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; cursor: pointer; }
.btn-confirm-reject { padding: 9px 20px; background: #ef4444; color: white; border: none; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; }
</style>
