<template>
  <div class="export-page">
    <div class="page-header">
      <h2>数据导出</h2>
      <p class="subtitle">导出平台数据为 Excel 文件</p>
    </div>

    <div class="export-container">
      <!-- 订单数据导出 -->
      <div class="export-card">
        <div class="card-icon orders">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/>
            <line x1="16" y1="17" x2="8" y2="17"/>
          </svg>
        </div>
        <div class="card-content">
          <h3>订单数据</h3>
          <p>导出所有订单信息，包括订单号、金额、状态等</p>
          <div class="filter-group">
            <div class="filter-item">
              <label>订单状态</label>
              <select v-model="filters.orderStatus">
                <option value="">全部</option>
                <option value="0">待付款</option>
                <option value="1">待发货</option>
                <option value="2">已发货</option>
                <option value="3">已完成</option>
                <option value="4">已取消</option>
                <option value="5">退款中</option>
              </select>
            </div>
            <div class="filter-item">
              <label>时间范围</label>
              <div class="date-range">
                <input v-model="filters.orderStartDate" type="date" />
                <span>至</span>
                <input v-model="filters.orderEndDate" type="date" />
              </div>
            </div>
          </div>
          <button class="btn-export" @click="exportOrders" :disabled="exporting.orders">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/>
              <polyline points="7 10 12 15 17 10"/>
              <line x1="12" y1="15" x2="12" y2="3"/>
            </svg>
            {{ exporting.orders ? '导出中...' : '导出订单数据' }}
          </button>
        </div>
      </div>

      <!-- 用户数据导出 -->
      <div class="export-card">
        <div class="card-icon users">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 00-3-3.87"/>
            <path d="M16 3.13a4 4 0 010 7.75"/>
          </svg>
        </div>
        <div class="card-content">
          <h3>用户数据</h3>
          <p>导出所有用户信息，包括用户名、邮箱、注册时间等</p>
          <div class="filter-group">
            <div class="filter-item">
              <label>用户状态</label>
              <select v-model="filters.userStatus">
                <option value="">全部</option>
                <option value="1">正常</option>
                <option value="0">禁用</option>
              </select>
            </div>
            <div class="filter-item">
              <label>注册时间</label>
              <div class="date-range">
                <input v-model="filters.userStartDate" type="date" />
                <span>至</span>
                <input v-model="filters.userEndDate" type="date" />
              </div>
            </div>
          </div>
          <button class="btn-export" @click="exportUsers" :disabled="exporting.users">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/>
              <polyline points="7 10 12 15 17 10"/>
              <line x1="12" y1="15" x2="12" y2="3"/>
            </svg>
            {{ exporting.users ? '导出中...' : '导出用户数据' }}
          </button>
        </div>
      </div>

      <!-- 商品数据导出 -->
      <div class="export-card">
        <div class="card-icon products">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/>
            <line x1="3" y1="6" x2="21" y2="6"/>
            <path d="M16 10a4 4 0 01-8 0"/>
          </svg>
        </div>
        <div class="card-content">
          <h3>商品数据</h3>
          <p>导出所有商品信息，包括商品名、价格、库存、销量等</p>
          <div class="filter-group">
            <div class="filter-item">
              <label>商品状态</label>
              <select v-model="filters.productStatus">
                <option value="">全部</option>
                <option value="1">在售</option>
                <option value="0">下架</option>
              </select>
            </div>
            <div class="filter-item">
              <label>审核状态</label>
              <select v-model="filters.auditStatus">
                <option value="">全部</option>
                <option value="1">已通过</option>
                <option value="0">审核中</option>
                <option value="2">已拒绝</option>
              </select>
            </div>
          </div>
          <button class="btn-export" @click="exportProducts" :disabled="exporting.products">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/>
              <polyline points="7 10 12 15 17 10"/>
              <line x1="12" y1="15" x2="12" y2="3"/>
            </svg>
            {{ exporting.products ? '导出中...' : '导出商品数据' }}
          </button>
        </div>
      </div>

      <!-- 商家数据导出 -->
      <div class="export-card">
        <div class="card-icon merchants">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
        </div>
        <div class="card-content">
          <h3>商家数据</h3>
          <p>导出所有商家信息，包括商家名、店铺名、联系方式等</p>
          <div class="filter-group">
            <div class="filter-item">
              <label>商家状态</label>
              <select v-model="filters.merchantStatus">
                <option value="">全部</option>
                <option value="1">正常</option>
                <option value="0">禁用</option>
              </select>
            </div>
            <div class="filter-item">
              <label>审核状态</label>
              <select v-model="filters.merchantAuditStatus">
                <option value="">全部</option>
                <option value="1">已通过</option>
                <option value="0">审核中</option>
                <option value="2">已拒绝</option>
              </select>
            </div>
          </div>
          <button class="btn-export" @click="exportMerchants" :disabled="exporting.merchants">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/>
              <polyline points="7 10 12 15 17 10"/>
              <line x1="12" y1="15" x2="12" y2="3"/>
            </svg>
            {{ exporting.merchants ? '导出中...' : '导出商家数据' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 导出历史 -->
    <div class="history-section">
      <h3>最近导出</h3>
      <div v-if="exportHistory.length > 0" class="history-list">
        <div v-for="(item, index) in exportHistory" :key="index" class="history-item">
          <div class="history-info">
            <span class="type-badge" :class="'type-' + item.type">{{ item.typeName }}</span>
            <span class="time">{{ formatDateTime(item.time) }}</span>
          </div>
          <a :href="item.url" class="btn-download" download>下载</a>
        </div>
      </div>
      <div v-else class="empty">
        <p>暂无导出记录</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as XLSX from 'xlsx'
import { 
  exportOrders as apiExportOrders, 
  exportUsers as apiExportUsers, 
  exportProducts as apiExportProducts, 
  exportMerchants as apiExportMerchants 
} from '@/api'

const exporting = reactive({
  orders: false,
  users: false,
  products: false,
  merchants: false
})

const filters = reactive({
  orderStatus: '',
  orderStartDate: '',
  orderEndDate: '',
  userStatus: '',
  userStartDate: '',
  userEndDate: '',
  productStatus: '',
  auditStatus: '',
  merchantStatus: '',
  merchantAuditStatus: ''
})

const exportHistory = ref([])

const formatDateTime = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const generateCSV = (headers, data) => {
  const csv = [headers.join(',')]
  for (const row of data) {
    csv.push(headers.map(h => {
      const value = row[h]
      if (value === null || value === undefined) return ''
      if (typeof value === 'string' && value.includes(',')) {
        return `"${value}"`
      }
      return value
    }).join(','))
  }
  return csv.join('\n')
}

const downloadFileXLSX = (data, filename) => {
  // 转换数据为二维数组
  const headers = Object.keys(data[0])
  const rows = [headers]
  for (const item of data) {
    rows.push(headers.map(h => item[h] || ''))
  }
  
  // 创建工作簿
  const ws = XLSX.utils.aoa_to_sheet(rows)
  
  // 自动调整列宽
  const colWidths = headers.map(h => {
    let maxLen = h.length
    for (const item of data) {
      const val = String(item[h] || '')
      if (val.length > maxLen) maxLen = val.length
    }
    return { wch: Math.min(maxLen + 2, 50) }
  })
  ws['!cols'] = colWidths
  
  // 冻结首行
  ws['!freeze'] = { xSplit: 0, ySplit: 1 }
  
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '数据')
  XLSX.writeFile(wb, filename)
}

const addToHistory = (type, typeName) => {
  const filename = `${type}_${new Date().getTime()}.csv`
  exportHistory.value.unshift({
    type,
    typeName,
    time: new Date(),
    url: '#'
  })
  if (exportHistory.value.length > 10) {
    exportHistory.value.pop()
  }
}

const exportOrders = async () => {
  exporting.orders = true
  try {
    const params = {}
    if (filters.orderStatus) params.status = filters.orderStatus
    if (filters.orderStartDate) params.startDate = filters.orderStartDate
    if (filters.orderEndDate) params.endDate = filters.orderEndDate
    
    const res = await apiExportOrders(params)
    if (res.code === 200 && res.data) {
      const data = Array.isArray(res.data) ? res.data : []
      if (data.length === 0) {
        ElMessage.warning('没有符合条件的数据')
        return
      }
      
      downloadFileXLSX(data, `订单数据_${new Date().getTime()}.xlsx`)
      
      addToHistory('orders', '订单数据')
      ElMessage.success(`订单数据导出成功，共 ${data.length} 条`)
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请稍后重试')
  } finally {
    exporting.orders = false
  }
}

const exportUsers = async () => {
  exporting.users = true
  try {
    const params = {}
    if (filters.userStatus) params.status = filters.userStatus
    if (filters.userStartDate) params.startDate = filters.userStartDate
    if (filters.userEndDate) params.endDate = filters.userEndDate
    
    const res = await apiExportUsers(params)
    if (res.code === 200 && res.data) {
      const data = Array.isArray(res.data) ? res.data : []
      if (data.length === 0) {
        ElMessage.warning('没有符合条件的数据')
        return
      }
      
      downloadFileXLSX(data, `用户数据_${new Date().getTime()}.xlsx`)
      
      addToHistory('users', '用户数据')
      ElMessage.success(`用户数据导出成功，共 ${data.length} 条`)
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请稍后重试')
  } finally {
    exporting.users = false
  }
}

const exportProducts = async () => {
  exporting.products = true
  try {
    const params = {}
    if (filters.productStatus) params.status = filters.productStatus
    if (filters.auditStatus) params.auditStatus = filters.auditStatus
    
    const res = await apiExportProducts(params)
    if (res.code === 200 && res.data) {
      const data = Array.isArray(res.data) ? res.data : []
      if (data.length === 0) {
        ElMessage.warning('没有符合条件的数据')
        return
      }
      
      downloadFileXLSX(data, `商品数据_${new Date().getTime()}.xlsx`)
      
      addToHistory('products', '商品数据')
      ElMessage.success(`商品数据导出成功，共 ${data.length} 条`)
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请稍后重试')
  } finally {
    exporting.products = false
  }
}

const exportMerchants = async () => {
  exporting.merchants = true
  try {
    const params = {}
    if (filters.merchantStatus) params.status = filters.merchantStatus
    if (filters.merchantAuditStatus) params.auditStatus = filters.merchantAuditStatus
    
    const res = await apiExportMerchants(params)
    if (res.code === 200 && res.data) {
      const data = Array.isArray(res.data) ? res.data : []
      if (data.length === 0) {
        ElMessage.warning('没有符合条件的数据')
        return
      }
      
      downloadFileXLSX(data, `商家数据_${new Date().getTime()}.xlsx`)
      
      addToHistory('merchants', '商家数据')
      ElMessage.success(`商家数据导出成功，共 ${data.length} 条`)
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请稍后重试')
  } finally {
    exporting.merchants = false
  }
}

onMounted(() => {
  // 加载导出历史
  const saved = localStorage.getItem('export_history')
  if (saved) {
    try {
      exportHistory.value = JSON.parse(saved)
    } catch (error) {
      console.error('加载导出历史失败:', error)
    }
  }
})
</script>

<style scoped>
.export-page { padding: 28px 32px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.page-header { margin-bottom: 24px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0 0 4px; }
.subtitle { font-size: 14px; color: #78716c; margin: 0; }
.export-container { display: grid; grid-template-columns: repeat(auto-fill, minmax(500px, 1fr)); gap: 16px; margin-bottom: 32px; }
.export-card { background: white; border-radius: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); padding: 20px; display: flex; gap: 16px; }
.card-icon { width: 60px; height: 60px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.card-icon svg { width: 32px; height: 32px; }
.card-icon.orders { background: #eff6ff; color: #3b82f6; }
.card-icon.users { background: #f0fdf4; color: #10b981; }
.card-icon.products { background: #fff7ed; color: #f97316; }
.card-icon.merchants { background: #fef3c7; color: #d97706; }
.card-content { flex: 1; }
.card-content h3 { font-size: 15px; font-weight: 600; color: #1c1917; margin: 0 0 4px; }
.card-content p { font-size: 13px; color: #78716c; margin: 0 0 12px; }
.filter-group { display: flex; gap: 12px; margin-bottom: 12px; flex-wrap: wrap; }
.filter-item { display: flex; flex-direction: column; gap: 4px; flex: 1; min-width: 150px; }
.filter-item label { font-size: 12px; color: #a8a29e; font-weight: 500; }
.filter-item select, .filter-item input { padding: 6px 8px; border: 1px solid #e7e5e4; border-radius: 6px; font-size: 12px; color: #44403c; outline: none; }
.filter-item select:focus, .filter-item input:focus { border-color: #1c1917; }
.date-range { display: flex; align-items: center; gap: 4px; }
.date-range input { flex: 1; padding: 6px 8px; border: 1px solid #e7e5e4; border-radius: 6px; font-size: 12px; }
.date-range span { color: #a8a29e; font-size: 12px; }
.btn-export { display: flex; align-items: center; gap: 6px; padding: 8px 16px; background: #1c1917; color: white; border: none; border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.15s; }
.btn-export:hover:not(:disabled) { background: #292524; }
.btn-export:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-export svg { width: 16px; height: 16px; }
.history-section { background: white; border-radius: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); padding: 20px; }
.history-section h3 { font-size: 15px; font-weight: 600; color: #1c1917; margin: 0 0 16px; }
.history-list { display: flex; flex-direction: column; gap: 8px; }
.history-item { display: flex; justify-content: space-between; align-items: center; padding: 12px; background: #fafaf9; border-radius: 8px; border: 1px solid #f5f5f4; }
.history-info { display: flex; align-items: center; gap: 12px; }
.type-badge { padding: 2px 8px; border-radius: 12px; font-size: 12px; font-weight: 500; }
.type-badge.type-orders { background: #eff6ff; color: #3b82f6; }
.type-badge.type-users { background: #f0fdf4; color: #10b981; }
.type-badge.type-products { background: #fff7ed; color: #f97316; }
.type-badge.type-merchants { background: #fef3c7; color: #d97706; }
.time { font-size: 12px; color: #a8a29e; }
.btn-download { padding: 6px 12px; background: #1c1917; color: white; border-radius: 6px; font-size: 12px; text-decoration: none; cursor: pointer; transition: all 0.15s; }
.btn-download:hover { background: #292524; }
.empty { text-align: center; padding: 40px 20px; color: #a8a29e; }
</style>

