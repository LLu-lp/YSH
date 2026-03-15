<template>
  <div class="shop-page">
    <div class="page-header">
      <h2>店铺管理</h2>
    </div>

    <div class="shop-content">
      <div class="info-card">
        <div class="card-header">
          <h3>基本信息</h3>
          <button class="btn-save" @click="handleSaveInfo" :disabled="saving">
            {{ saving ? '保存中...' : '保存修改' }}
          </button>
        </div>
        <div class="form-grid">
          <div class="form-row full">
            <div class="avatar-upload">
              <div class="shop-avatar">
                <img v-if="shopInfo.logo" :src="shopInfo.logo" alt="店铺logo" />
                <span v-else>{{ shopInfo.name?.charAt(0) || 'S' }}</span>
              </div>
              <div class="avatar-info">
                <p class="avatar-label">店铺 Logo</p>
                <input v-model="shopInfo.logo" type="text" class="input-url" placeholder="请输入Logo图片URL" />
                <p class="avatar-tip">建议尺寸 200×200，支持 JPG/PNG</p>
              </div>
            </div>
          </div>
          <div class="form-item">
            <label>店铺名称 <span class="required">*</span></label>
            <input v-model="shopInfo.name" type="text" placeholder="请输入店铺名称" />
          </div>
          <div class="form-item">
            <label>联系电话 <span class="required">*</span></label>
            <input v-model="shopInfo.phone" type="text" placeholder="请输入联系电话" />
          </div>
          <div class="form-item full">
            <label>店铺地址</label>
            <input v-model="shopInfo.address" type="text" placeholder="请输入店铺地址" />
          </div>
          <div class="form-item full">
            <label>店铺简介</label>
            <textarea v-model="shopInfo.description" rows="4" placeholder="请输入店铺简介"></textarea>
          </div>
        </div>
      </div>

      <div class="status-card">
        <div class="card-header">
          <h3>店铺状态</h3>
        </div>
        <div class="status-items">
          <div class="status-item">
            <div class="status-dot" :class="shopInfo.status === 1 ? 'active' : 'inactive'"></div>
            <div>
              <p class="status-name">运营状态</p>
              <p class="status-desc">{{ shopInfo.status === 1 ? '正常营业' : '暂停营业' }}</p>
            </div>
          </div>
          <div class="status-item">
            <div class="status-dot" :class="shopInfo.auditStatus === 1 ? 'active' : 'pending'"></div>
            <div>
              <p class="status-name">审核状态</p>
              <p class="status-desc">{{ auditStatusText }}</p>
            </div>
          </div>
          <div class="status-item stats">
            <div class="stat-value">{{ shopInfo.rating || 5.0 }}</div>
            <div class="stat-label">店铺评分</div>
          </div>
          <div class="status-item stats">
            <div class="stat-value">{{ shopInfo.salesCount || 0 }}</div>
            <div class="stat-label">总销量</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getShopInfo, updateShopInfo } from '@/api'

const saving = ref(false)
const shopInfo = ref({
  id: null,
  name: '',
  logo: '',
  description: '',
  address: '',
  phone: '',
  status: 1,
  auditStatus: 0,
  rating: 5.0,
  salesCount: 0
})

const auditStatusText = computed(() => {
  const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return map[shopInfo.value.auditStatus] || '未知'
})

const loadShopInfo = async () => {
  try {
    const res = await getShopInfo()
    if (res.code === 200 && res.data) {
      shopInfo.value = res.data
    }
  } catch (error) {
    console.error('加载店铺信息失败:', error)
  }
}

const handleSaveInfo = async () => {
  if (!shopInfo.value.name || !shopInfo.value.phone) {
    ElMessage.warning('请填写必填项')
    return
  }
  saving.value = true
  try {
    await updateShopInfo({
      name: shopInfo.value.name,
      logo: shopInfo.value.logo,
      description: shopInfo.value.description,
      address: shopInfo.value.address,
      phone: shopInfo.value.phone
    })
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存店铺信息失败:', error)
  } finally {
    saving.value = false
  }
}

onMounted(() => loadShopInfo())
</script>

<style scoped>
.shop-page { padding: 28px 32px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.page-header { margin-bottom: 24px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0; }
.shop-content { display: grid; grid-template-columns: 1fr 280px; gap: 20px; align-items: start; }
.info-card, .status-card { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.card-header h3 { font-size: 15px; font-weight: 600; color: #1c1917; margin: 0; }
.btn-save { padding: 8px 20px; background: #f97316; color: white; border: none; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; }
.btn-save:hover { background: #ea580c; }
.btn-save:disabled { background: #fdba74; cursor: not-allowed; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.form-item { display: flex; flex-direction: column; gap: 6px; }
.form-item.full { grid-column: 1 / -1; }
.form-row.full { grid-column: 1 / -1; }
.form-item label { font-size: 13px; color: #78716c; font-weight: 500; }
.required { color: #ef4444; }
.form-item input, .form-item textarea { padding: 10px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 14px; color: #1c1917; outline: none; resize: vertical; font-family: inherit; }
.form-item input:focus, .form-item textarea:focus { border-color: #f97316; }
.avatar-upload { display: flex; align-items: center; gap: 16px; padding: 16px; background: #fafaf9; border-radius: 10px; }
.shop-avatar { width: 72px; height: 72px; border-radius: 12px; background: #f97316; color: white; font-size: 28px; font-weight: 700; display: flex; align-items: center; justify-content: center; overflow: hidden; flex-shrink: 0; }
.shop-avatar img { width: 100%; height: 100%; object-fit: cover; }
.avatar-label { font-size: 13px; color: #44403c; font-weight: 500; margin: 0 0 8px; }
.input-url { padding: 8px 12px; border: 1.5px solid #e7e5e4; border-radius: 6px; font-size: 13px; width: 280px; }
.avatar-tip { font-size: 11px; color: #a8a29e; margin: 6px 0 0; }
.status-items { display: flex; flex-direction: column; gap: 12px; }
.status-item { display: flex; align-items: center; gap: 12px; padding: 14px; background: #fafaf9; border-radius: 10px; }
.status-item.stats { flex-direction: column; text-align: center; gap: 4px; }
.status-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
.status-dot.active { background: #10b981; box-shadow: 0 0 0 3px #d1fae5; }
.status-dot.inactive { background: #a8a29e; }
.status-dot.pending { background: #f59e0b; box-shadow: 0 0 0 3px #fef3c7; }
.status-item > div:nth-child(2) { flex: 1; }
.status-name { font-size: 13px; font-weight: 600; color: #1c1917; margin: 0 0 2px; }
.status-desc { font-size: 12px; color: #a8a29e; margin: 0; }
.stat-value { font-size: 24px; font-weight: 700; color: #f97316; }
.stat-label { font-size: 12px; color: #78716c; }
</style>
