<template>
  <div class="merchants-page">
    <div class="page-header">
      <h2>商家管理</h2>
    </div>

    <div class="status-tabs">
      <button v-for="tab in tabs" :key="tab.value" class="tab-btn" :class="{ active: activeTab === tab.value }" @click="switchTab(tab.value)">
        {{ tab.label }}<span v-if="tab.count" class="tab-count">{{ tab.count }}</span>
      </button>
    </div>

    <div class="filter-bar">
      <input v-model="filters.keyword" type="text" placeholder="搜索店铺名称 / 商家账号..." @keyup.enter="loadList" />
      <button class="btn-search" @click="loadList">搜索</button>
    </div>

    <div class="table-card" v-loading="loading">
      <table class="data-table">
        <thead>
          <tr>
            <th>商家信息</th>
            <th>联系方式</th>
            <th>商品数</th>
            <th>总销售额</th>
            <th>入驻时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="merchant in merchants" :key="merchant.id">
            <td>
              <div class="merchant-cell">
                <div class="merchant-avatar">{{ (merchant.shopName || merchant.name || 'S').charAt(0) }}</div>
                <div>
                  <p class="shop-name">{{ merchant.shopName || merchant.name || '-' }}</p>
                  <p class="shop-user">账号：{{ merchant.username }}</p>
                </div>
              </div>
            </td>
            <td>{{ merchant.phone || '-' }}</td>
            <td>{{ merchant.productCount || 0 }}</td>
            <td class="price">¥{{ formatPrice(merchant.totalSales) }}</td>
            <td class="time">{{ formatDateTime(merchant.createTime) }}</td>
            <td><span class="status-tag" :class="statusMap[getDisplayStatus(merchant)]?.cls">{{ statusMap[getDisplayStatus(merchant)]?.text }}</span></td>
            <td>
              <div class="action-btns">
                <button class="btn-sm approve" @click="approveMerchant(merchant)" v-if="canApprove(merchant)">通过</button>
                <button class="btn-sm reject" @click="rejectMerchant(merchant)" v-if="canReject(merchant)">拒绝</button>
                <button class="btn-sm ban" @click="banMerchant(merchant)" v-if="canBan(merchant)">封禁</button>
                <button class="btn-sm unban" @click="unbanMerchant(merchant)" v-if="canUnban(merchant)">解封</button>
                <button class="btn-sm view" @click="viewMerchant(merchant)">详情</button>
              </div>
            </td>
          </tr>
          <tr v-if="merchants.length === 0 && !loading">
            <td colspan="7" style="text-align: center; color: #a8a29e; padding: 40px;">暂无商家数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <button @click="page > 1 && (page--, loadList())" :disabled="page === 1">上一页</button>
        <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
        <button @click="page < totalPages && (page++, loadList())" :disabled="page === totalPages">下一页</button>
      </div>
    </div>

    <div v-if="showDetail" class="modal-mask" @click.self="showDetail = false">
      <div class="modal">
        <div class="modal-header">
          <h3>商家详情</h3>
          <button @click="showDetail = false" class="modal-close">×</button>
        </div>
        <div class="modal-body" v-if="currentMerchant">
          <div class="detail-item"><label>店铺名称：</label><span>{{ currentMerchant.shopName || currentMerchant.name }}</span></div>
          <div class="detail-item"><label>商家账号：</label><span>{{ currentMerchant.username }}</span></div>
          <div class="detail-item"><label>联系电话：</label><span>{{ currentMerchant.phone || '-' }}</span></div>
          <div class="detail-item"><label>邮箱：</label><span>{{ currentMerchant.email || '-' }}</span></div>
          <div class="detail-item"><label>入驻时间：</label><span>{{ formatDateTime(currentMerchant.createTime) }}</span></div>
          <div class="detail-item"><label>状态：</label><span class="status-tag" :class="statusMap[getDisplayStatus(currentMerchant)]?.cls">{{ statusMap[getDisplayStatus(currentMerchant)]?.text }}</span></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGetMerchants, reviewMerchant, freezeMerchant } from '@/api'

const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)
const activeTab = ref('')
const filters = ref({ keyword: '' })
const showDetail = ref(false)
const currentMerchant = ref(null)

const tabs = ref([
  { label: '全部商家', value: '', count: 0 },
  { label: '待审核', value: '0', count: 0 },
  { label: '正常营业', value: '1', count: 0 },
  { label: '已拒绝', value: '2', count: 0 },
  { label: '已封禁', value: '3', count: 0 }
])

const statusMap = {
  0: { text: '待审核', cls: 'pending' },
  1: { text: '正常营业', cls: 'success' },
  2: { text: '已拒绝', cls: 'danger' },
  3: { text: '已封禁', cls: 'ban' }
}

const getDisplayStatus = (merchant) => {
  // 如果审核未通过，显示审核状态；否则显示运营状态
  const auditStatus = merchant.auditStatus ?? 0
  const shopStatus = merchant.shopStatus ?? 1
  
  if (auditStatus !== 1) {
    return auditStatus
  }
  return shopStatus
}

const canApprove = (merchant) => {
  return (merchant.auditStatus ?? 0) === 0
}

const canReject = (merchant) => {
  return (merchant.auditStatus ?? 0) === 0
}

const canBan = (merchant) => {
  return (merchant.auditStatus ?? 0) === 1 && (merchant.shopStatus ?? 1) === 1
}

const canUnban = (merchant) => {
  return (merchant.auditStatus ?? 0) === 1 && (merchant.shopStatus ?? 1) === 3
}

const merchants = ref([])

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

const formatPrice = (price) => {
  if (!price) return '0.00'
  return parseFloat(price).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const switchTab = (val) => { 
  activeTab.value = val
  page.value = 1
  loadList()
}

const loadList = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize,
      status: activeTab.value !== '' ? parseInt(activeTab.value) : undefined,
      keyword: filters.value.keyword || undefined
    }
    const res = await adminGetMerchants(params)
    if (res.code === 200 && res.data) {
      if (res.data.content) {
        merchants.value = res.data.content
        total.value = res.data.totalElements || 0
      } else if (Array.isArray(res.data)) {
        merchants.value = res.data
        total.value = res.data.length
      }
    }
  } catch (error) {
    console.error('加载商家列表失败:', error)
  } finally {
    loading.value = false
  }
}

const approveMerchant = async (m) => {
  if (!confirm(`确认通过「${m.shopName || m.name}」的入驻申请？`)) return
  try {
    await reviewMerchant(m.id, { auditStatus: 1 })
    ElMessage.success('审核通过')
    page.value = 1
    loadList()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

const rejectMerchant = async (m) => {
  const reason = prompt('请输入拒绝原因：')
  if (!reason) return
  try {
    await reviewMerchant(m.id, { auditStatus: 2, auditRemark: reason })
    ElMessage.success('已拒绝申请')
    page.value = 1
    loadList()
  } catch (error) {
    console.error('拒绝失败:', error)
    ElMessage.error('拒绝失败')
  }
}

const banMerchant = async (m) => {
  if (!confirm(`确认封禁商家「${m.shopName || m.name}」？`)) return
  try {
    await freezeMerchant(m.id, { status: 3 })
    ElMessage.success('已封禁')
    page.value = 1
    loadList()
  } catch (error) {
    console.error('封禁失败:', error)
    ElMessage.error('封禁失败')
  }
}

const unbanMerchant = async (m) => {
  if (!confirm(`确认解封商家「${m.shopName || m.name}」？`)) return
  try {
    await freezeMerchant(m.id, { status: 1 })
    ElMessage.success('已解封')
    page.value = 1
    loadList()
  } catch (error) {
    console.error('解封失败:', error)
    ElMessage.error('解封失败')
  }
}

const viewMerchant = (m) => {
  currentMerchant.value = m
  showDetail.value = true
}

onMounted(() => loadList())
</script>

<style scoped>
.merchants-page { padding: 28px 32px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0; }
.status-tabs { display: flex; gap: 4px; margin-bottom: 16px; border-bottom: 1px solid #e7e5e4; }
.tab-btn { padding: 10px 16px; background: none; border: none; font-size: 13px; color: #78716c; cursor: pointer; border-bottom: 2px solid transparent; margin-bottom: -1px; display: flex; align-items: center; gap: 6px; }
.tab-btn:hover { color: #1c1917; }
.tab-btn.active { color: #1c1917; border-bottom-color: #1c1917; font-weight: 600; }
.tab-count { background: #ef4444; color: white; font-size: 11px; padding: 1px 6px; border-radius: 10px; font-weight: 700; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.filter-bar input { padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; color: #44403c; outline: none; min-width: 260px; }
.filter-bar input:focus { border-color: #1c1917; }
.btn-search { padding: 9px 18px; background: #1c1917; color: white; border: none; border-radius: 8px; font-size: 13px; cursor: pointer; }
.table-card { background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th { text-align: left; padding: 14px 16px; color: #a8a29e; font-weight: 500; background: #fafaf9; border-bottom: 1px solid #f5f5f4; }
.data-table td { padding: 12px 16px; color: #44403c; border-bottom: 1px solid #f5f5f4; vertical-align: middle; }
.data-table tr:last-child td { border-bottom: none; }
.merchant-cell { display: flex; align-items: center; gap: 10px; }
.merchant-avatar { width: 36px; height: 36px; background: #1c1917; color: white; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 14px; flex-shrink: 0; }
.shop-name { font-size: 13px; font-weight: 600; color: #1c1917; margin: 0 0 2px; }
.shop-user { font-size: 11px; color: #a8a29e; margin: 0; }
.price { font-weight: 600; color: #f97316; }
.time { color: #a8a29e; font-size: 12px; }
.status-tag { padding: 3px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.status-tag.pending { background: #fefce8; color: #ca8a04; }
.status-tag.success { background: #f0fdf4; color: #10b981; }
.status-tag.danger { background: #fef2f2; color: #ef4444; }
.status-tag.ban { background: #f5f5f4; color: #a8a29e; }
.action-btns { display: flex; gap: 6px; flex-wrap: wrap; }
.btn-sm { padding: 5px 10px; border-radius: 6px; font-size: 12px; cursor: pointer; border: 1.5px solid; font-weight: 500; }
.btn-sm.approve { border-color: #d1fae5; color: #10b981; background: #f0fdf4; }
.btn-sm.approve:hover { background: #10b981; color: white; }
.btn-sm.reject { border-color: #fecaca; color: #ef4444; background: #fef2f2; }
.btn-sm.reject:hover { background: #ef4444; color: white; }
.btn-sm.ban { border-color: #fed7aa; color: #f97316; background: #fff7ed; }
.btn-sm.ban:hover { background: #f97316; color: white; }
.btn-sm.unban { border-color: #d1fae5; color: #10b981; background: #f0fdf4; }
.btn-sm.unban:hover { background: #10b981; color: white; }
.btn-sm.view { border-color: #e7e5e4; color: #78716c; background: #fafaf9; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 12px; padding: 14px 16px; border-top: 1px solid #f5f5f4; font-size: 13px; color: #78716c; }
.pagination button { padding: 6px 14px; border: 1.5px solid #e7e5e4; border-radius: 6px; background: white; font-size: 13px; cursor: pointer; }
.pagination button:hover:not(:disabled) { border-color: #1c1917; color: #1c1917; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: white; border-radius: 14px; width: 440px; overflow: hidden; }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 18px 22px; border-bottom: 1px solid #f5f5f4; }
.modal-header h3 { font-size: 16px; font-weight: 600; color: #1c1917; margin: 0; }
.modal-close { background: none; border: none; font-size: 22px; color: #a8a29e; cursor: pointer; }
.modal-body { padding: 20px 22px; }
.detail-item { display: flex; align-items: center; padding: 8px 0; font-size: 14px; border-bottom: 1px solid #f3f4f6; }
.detail-item label { width: 90px; color: #6b7280; }
.detail-item span { color: #1f2937; }
</style>
