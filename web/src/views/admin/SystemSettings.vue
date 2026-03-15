<template>
  <div class="settings-page">
    <div class="page-header">
      <h2>系统设置</h2>
      <p class="subtitle">管理平台基础配置</p>
    </div>

    <div class="settings-container">
      <!-- 基础设置 -->
      <div class="settings-card">
        <div class="card-header">
          <h3>基础设置</h3>
        </div>
        <div class="card-body">
          <div class="form-item">
            <label>平台名称</label>
            <input v-model="settings.platformName" type="text" placeholder="请输入平台名称" />
          </div>
          <div class="form-item">
            <label>平台描述</label>
            <textarea v-model="settings.platformDesc" rows="3" placeholder="请输入平台描述"></textarea>
          </div>
          <div class="form-item">
            <label>平台Logo URL</label>
            <input v-model="settings.platformLogo" type="text" placeholder="请输入Logo URL" />
          </div>
          <div class="form-item">
            <label>联系电话</label>
            <input v-model="settings.contactPhone" type="text" placeholder="请输入联系电话" />
          </div>
          <div class="form-item">
            <label>联系邮箱</label>
            <input v-model="settings.contactEmail" type="email" placeholder="请输入联系邮箱" />
          </div>
        </div>
      </div>

      <!-- 交易设置 -->
      <div class="settings-card">
        <div class="card-header">
          <h3>交易设置</h3>
        </div>
        <div class="card-body">
          <div class="form-row">
            <div class="form-item">
              <label>默认运费（元）</label>
              <div class="input-group">
                <span class="prefix">¥</span>
                <input v-model="settings.defaultShipping" type="number" step="0.01" min="0" placeholder="0.00" />
              </div>
            </div>
            <div class="form-item">
              <label>免运费金额（元）</label>
              <div class="input-group">
                <span class="prefix">¥</span>
                <input v-model="settings.freeShippingAmount" type="number" step="0.01" min="0" placeholder="0.00" />
              </div>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>订单超时时间（分钟）</label>
              <input v-model="settings.orderTimeout" type="number" min="1" step="1" placeholder="30" />
            </div>
            <div class="form-item">
              <label>退款处理时间（天）</label>
              <input v-model="settings.refundDays" type="number" min="1" step="1" placeholder="7" />
            </div>
          </div>
        </div>
      </div>

      <!-- 商家设置 -->
      <div class="settings-card">
        <div class="card-header">
          <h3>商家设置</h3>
        </div>
        <div class="card-body">
          <div class="form-row">
            <div class="form-item">
              <label>商家入驻保证金（元）</label>
              <div class="input-group">
                <span class="prefix">¥</span>
                <input v-model="settings.merchantDeposit" type="number" step="0.01" min="0" placeholder="0.00" />
              </div>
            </div>
            <div class="form-item">
              <label>平台佣金比例（%）</label>
              <div class="input-group">
                <input v-model="settings.platformCommission" type="number" step="0.01" min="0" max="100" placeholder="0.00" />
                <span class="suffix">%</span>
              </div>
            </div>
          </div>
          <div class="form-item">
            <label>商家审核是否需要人工审核</label>
            <div class="checkbox-group">
              <label class="checkbox">
                <input v-model="settings.merchantAuditRequired" type="checkbox" />
                <span>启用人工审核</span>
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- 用户设置 -->
      <div class="settings-card">
        <div class="card-header">
          <h3>用户设置</h3>
        </div>
        <div class="card-body">
          <div class="form-item">
            <label>新用户注册赠送优惠券</label>
            <div class="checkbox-group">
              <label class="checkbox">
                <input v-model="settings.newUserCoupon" type="checkbox" />
                <span>启用新用户优惠券</span>
              </label>
            </div>
          </div>
          <div class="form-item">
            <label>新用户优惠券金额（元）</label>
            <div class="input-group">
              <span class="prefix">¥</span>
              <input v-model="settings.newUserCouponAmount" type="number" step="0.01" min="0" placeholder="0.00" />
            </div>
          </div>
          <div class="form-item">
            <label>账户锁定阈值（登录失败次数）</label>
            <input v-model="settings.accountLockThreshold" type="number" min="1" step="1" placeholder="5" />
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button class="btn-reset" @click="handleReset">重置</button>
        <button class="btn-save" @click="handleSave" :disabled="saving">
          {{ saving ? '保存中...' : '保存设置' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSystemSettings, saveSystemSettings, resetSystemSettings } from '@/api'

const saving = ref(false)
const loading = ref(false)
const originalSettings = ref({})

const settings = reactive({
  platformName: '好物购买系统',
  platformDesc: '一个功能完整的电商平台',
  platformLogo: '',
  contactPhone: '400-123-4567',
  contactEmail: 'support@example.com',
  defaultShipping: 10,
  freeShippingAmount: 100,
  orderTimeout: 30,
  refundDays: 7,
  merchantDeposit: 1000,
  platformCommission: 5,
  merchantAuditRequired: true,
  newUserCoupon: true,
  newUserCouponAmount: 10,
  accountLockThreshold: 5
})

const loadSettings = async () => {
  loading.value = true
  try {
    const res = await getSystemSettings()
    if (res.code === 200 && res.data) {
      Object.assign(settings, res.data)
      Object.assign(originalSettings.value, res.data)
    }
  } catch (error) {
    console.error('加载设置失败:', error)
    ElMessage.error('加载设置失败')
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    const res = await saveSystemSettings(settings)
    if (res.code === 200) {
      Object.assign(originalSettings.value, settings)
      ElMessage.success('设置已保存')
    }
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const handleReset = async () => {
  if (confirm('确定要重置所有设置吗？')) {
    try {
      const res = await resetSystemSettings()
      if (res.code === 200 && res.data) {
        Object.assign(settings, res.data)
        Object.assign(originalSettings.value, res.data)
        ElMessage.success('已重置为默认设置')
      }
    } catch (error) {
      console.error('重置设置失败:', error)
      ElMessage.error('重置失败，请稍后重试')
    }
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.settings-page { padding: 28px 32px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.page-header { margin-bottom: 24px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1c1917; margin: 0 0 4px; }
.subtitle { font-size: 14px; color: #78716c; margin: 0; }
.settings-container { max-width: 900px; }
.settings-card { background: white; border-radius: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); margin-bottom: 16px; overflow: hidden; }
.card-header { padding: 16px 20px; border-bottom: 1px solid #f5f5f4; background: #fafaf9; }
.card-header h3 { font-size: 15px; font-weight: 600; color: #1c1917; margin: 0; }
.card-body { padding: 20px; }
.form-item { margin-bottom: 16px; }
.form-item label { display: block; font-size: 13px; color: #78716c; font-weight: 500; margin-bottom: 6px; }
.form-item input, .form-item textarea { width: 100%; padding: 9px 12px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; color: #1c1917; outline: none; box-sizing: border-box; font-family: inherit; }
.form-item input:focus, .form-item textarea:focus { border-color: #1c1917; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.input-group { position: relative; display: flex; align-items: center; }
.prefix { position: absolute; left: 12px; font-size: 13px; color: #78716c; font-weight: 500; }
.input-group input { padding-left: 32px; width: 100%; padding: 9px 20px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 13px; color: #1c1917; outline: none; box-sizing: border-box; }
.input-group input:focus { border-color: #1c1917; }
.suffix { position: absolute; right: 12px; font-size: 13px; color: #78716c; font-weight: 500; }
.checkbox-group { display: flex; flex-direction: column; gap: 8px; }
.checkbox { display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 13px; color: #44403c; }
.checkbox input { cursor: pointer; }
.action-buttons { display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px; }
.btn-reset { padding: 10px 24px; background: white; color: #78716c; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.15s; }
.btn-reset:hover { background: #f5f5f4; }
.btn-save { padding: 10px 24px; background: #1c1917; color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.15s; }
.btn-save:hover:not(:disabled) { background: #292524; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
</style>

