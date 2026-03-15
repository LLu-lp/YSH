<template>
  <div class="coupon-manage">
    <div class="page-header">
      <h2>优惠券管理</h2>
      <button class="btn-add" @click="openCreateModal">+ 创建优惠券</button>
    </div>

    <div class="coupon-list" v-loading="loading">
      <div v-for="coupon in coupons" :key="coupon.id" class="coupon-card">
        <div class="coupon-header">
          <div class="coupon-info">
            <h3 class="coupon-name">{{ coupon.name }}</h3>
            <p class="coupon-desc">满¥{{ coupon.minAmount }}可用</p>
          </div>
          <div class="coupon-value">
            <span class="value">¥{{ coupon.value }}</span>
            <span class="unit">优惠</span>
          </div>
        </div>
        <div class="coupon-body">
          <div class="coupon-stat">
            <span class="label">有效期</span>
            <span class="value">{{ formatDate(coupon.startTime) }} ~ {{ formatDate(coupon.endTime) }}</span>
          </div>
          <div class="coupon-stat">
            <span class="label">剩余/总量</span>
            <span class="value">{{ coupon.remainingCount }} / {{ coupon.totalCount }}</span>
          </div>
          <div class="coupon-stat">
            <span class="label">状态</span>
            <span class="status-tag" :class="coupon.status === 1 ? 'active' : 'inactive'">
              {{ coupon.status === 1 ? '启用' : '禁用' }}
            </span>
          </div>
        </div>
        <div class="coupon-footer">
          <button class="btn-action edit" @click="openEditModal(coupon)">编辑</button>
          <button class="btn-action delete" @click="handleDelete(coupon)">删除</button>
        </div>
      </div>
      <div v-if="coupons.length === 0 && !loading" class="empty-state">
        <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect x="8" y="16" width="48" height="32" rx="4" stroke="#d1d5db" stroke-width="2"/>
          <path d="M16 24h32M16 32h32M16 40h20" stroke="#d1d5db" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <p>暂无优惠券</p>
        <button class="btn-create" @click="openCreateModal">创建第一张优惠券</button>
      </div>
    </div>

    <!-- 创建/编辑优惠券弹窗 -->
    <div v-if="showModal" class="modal-mask" @click.self="showModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ editingCoupon ? '编辑优惠券' : '创建优惠券' }}</h3>
          <button @click="showModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>优惠券名称 <span class="required">*</span></label>
            <input v-model="form.name" type="text" placeholder="例如：新年优惠券" maxlength="50" />
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>优惠金额 <span class="required">*</span></label>
              <div class="input-group">
                <span class="prefix">¥</span>
                <input v-model="form.value" type="number" step="0.01" min="0.01" placeholder="0.00" />
              </div>
            </div>
            <div class="form-item">
              <label>使用门槛 <span class="required">*</span></label>
              <div class="input-group">
                <span class="prefix">满¥</span>
                <input v-model="form.minAmount" type="number" step="0.01" min="0" placeholder="0.00" />
              </div>
              <p class="form-tip">0 表示无门槛</p>
            </div>
          </div>
          <div class="form-item">
            <label>有效期 <span class="required">*</span></label>
            <div class="date-range">
              <input v-model="form.startTime" type="date" />
              <span class="separator">至</span>
              <input v-model="form.endTime" type="date" />
            </div>
          </div>
          <div class="form-item">
            <label>发放总量 <span class="required">*</span></label>
            <input v-model="form.totalCount" type="number" min="1" step="1" placeholder="请输入数量" />
            <p class="form-tip">创建后不可修改</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showModal = false">取消</button>
          <button class="btn-confirm" @click="handleSave" :disabled="saving">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantCoupons, createMerchantCoupon, deleteMerchantCoupon } from '@/api'

const loading = ref(false)
const saving = ref(false)
const showModal = ref(false)
const coupons = ref([])
const editingCoupon = ref(null)

const form = reactive({
  name: '',
  value: 5,
  minAmount: 0,
  startTime: '',
  endTime: '',
  totalCount: 100
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const loadCoupons = async () => {
  loading.value = true
  try {
    const res = await getMerchantCoupons()
    if (res.code === 200 && res.data) {
      coupons.value = Array.isArray(res.data) ? res.data : res.data.content || []
    }
  } catch (error) {
    console.error('加载优惠券失败:', error)
    ElMessage.error('加载优惠券失败')
  } finally {
    loading.value = false
  }
}

const openCreateModal = () => {
  editingCoupon.value = null
  form.name = ''
  form.value = 5
  form.minAmount = 0
  form.startTime = ''
  form.endTime = ''
  form.totalCount = 100
  showModal.value = true
}

const openEditModal = (coupon) => {
  editingCoupon.value = coupon
  form.name = coupon.name
  form.value = coupon.value
  form.minAmount = coupon.minAmount
  form.startTime = coupon.startTime ? coupon.startTime.split('T')[0] : ''
  form.endTime = coupon.endTime ? coupon.endTime.split('T')[0] : ''
  form.totalCount = coupon.totalCount
  showModal.value = true
}

const handleSave = async () => {
  if (!form.name || !form.value || !form.startTime || !form.endTime || !form.totalCount) {
    ElMessage.warning('请填写所有必填项')
    return
  }

  if (new Date(form.startTime) >= new Date(form.endTime)) {
    ElMessage.warning('开始时间必须早于结束时间')
    return
  }

  saving.value = true
  try {
    const data = {
      name: form.name,
      value: parseFloat(form.value),
      minAmount: parseFloat(form.minAmount),
      startTime: new Date(form.startTime).toISOString(),
      endTime: new Date(form.endTime).toISOString(),
      totalCount: parseInt(form.totalCount),
      remainingCount: editingCoupon.value ? editingCoupon.value.remainingCount : parseInt(form.totalCount)
    }

    const res = await createMerchantCoupon(data)
    if (res.code === 200) {
      ElMessage.success(editingCoupon.value ? '优惠券已更新' : '优惠券已创建')
      showModal.value = false
      loadCoupons()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存优惠券失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const handleDelete = async (coupon) => {
  try {
    await ElMessageBox.confirm(`确定删除优惠券「${coupon.name}」吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })

    const res = await deleteMerchantCoupon(coupon.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadCoupons()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除优惠券失败:', error)
    }
  }
}

onMounted(() => loadCoupons())
</script>

<style scoped>
.coupon-manage { padding: 28px 32px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0; }
.btn-add { padding: 10px 20px; background: #f97316; color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.15s; }
.btn-add:hover { background: #ea580c; }
.coupon-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }
.coupon-card { background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06); transition: all 0.15s; display: flex; flex-direction: column; }
.coupon-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.coupon-header { display: flex; justify-content: space-between; align-items: flex-start; padding: 16px; background: linear-gradient(135deg, #fff7ed 0%, #fef3c7 100%); border-bottom: 1px solid #fed7aa; }
.coupon-info { flex: 1; }
.coupon-name { font-size: 15px; font-weight: 600; color: #1c1917; margin: 0 0 4px; }
.coupon-desc { font-size: 12px; color: #78716c; margin: 0; }
.coupon-value { text-align: right; }
.value { display: block; font-size: 24px; font-weight: 700; color: #f97316; line-height: 1; }
.unit { display: block; font-size: 12px; color: #78716c; margin-top: 2px; }
.coupon-body { padding: 12px 16px; flex: 1; }
.coupon-stat { display: flex; justify-content: space-between; align-items: center; padding: 6px 0; font-size: 13px; }
.coupon-stat .label { color: #a8a29e; }
.coupon-stat .value { color: #44403c; font-weight: 500; }
.status-tag { padding: 2px 8px; border-radius: 12px; font-size: 12px; font-weight: 500; }
.status-tag.active { background: #f0fdf4; color: #10b981; }
.status-tag.inactive { background: #f5f5f4; color: #a8a29e; }
.coupon-footer { display: flex; gap: 8px; padding: 12px 16px; border-top: 1px solid #f5f5f4; }
.btn-action { flex: 1; padding: 8px 12px; border-radius: 6px; font-size: 13px; font-weight: 500; cursor: pointer; border: 1.5px solid; transition: all 0.15s; }
.btn-action.edit { background: #eff6ff; color: #3b82f6; border-color: #dbeafe; }
.btn-action.edit:hover { background: #dbeafe; }
.btn-action.delete { background: #fef2f2; color: #ef4444; border-color: #fecaca; }
.btn-action.delete:hover { background: #fecaca; }
.empty-state { grid-column: 1 / -1; text-align: center; padding: 60px 20px; }
.empty-state svg { width: 80px; height: 80px; margin-bottom: 16px; opacity: 0.5; }
.empty-state p { font-size: 16px; color: #78716c; margin: 0 0 16px; }
.btn-create { padding: 10px 24px; background: #f97316; color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: white; border-radius: 14px; width: 500px; overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; border-bottom: 1px solid #f5f5f4; }
.modal-header h3 { font-size: 16px; font-weight: 600; color: #1c1917; margin: 0; }
.modal-close { background: none; border: none; font-size: 22px; color: #a8a29e; cursor: pointer; }
.modal-body { padding: 20px 24px; max-height: 60vh; overflow-y: auto; }
.form-item { margin-bottom: 16px; }
.form-item label { display: block; font-size: 13px; color: #78716c; font-weight: 500; margin-bottom: 6px; }
.required { color: #ef4444; }
.form-item input { width: 100%; padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; color: #1c1917; outline: none; box-sizing: border-box; }
.form-item input:focus { border-color: #f97316; }
.form-tip { font-size: 12px; color: #a8a29e; margin-top: 4px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.input-group { position: relative; display: flex; align-items: center; }
.prefix { position: absolute; left: 12px; font-size: 13px; color: #78716c; font-weight: 500; }
.input-group input { padding-left: 32px; }
.date-range { display: flex; align-items: center; gap: 8px; }
.date-range input { flex: 1; padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; color: #1c1917; outline: none; }
.date-range input:focus { border-color: #f97316; }
.separator { color: #a8a29e; font-weight: 500; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 24px; border-top: 1px solid #f5f5f4; }
.btn-cancel { padding: 9px 20px; background: white; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; cursor: pointer; }
.btn-confirm { padding: 9px 20px; background: #f97316; color: white; border: none; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; }
.btn-confirm:disabled { opacity: 0.6; cursor: not-allowed; }
</style>

