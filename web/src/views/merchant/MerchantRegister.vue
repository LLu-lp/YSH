<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="40" height="40" rx="10" fill="#f97316"/>
            <path d="M10 28l4-12h12l4 12H10z" fill="white" opacity="0.9"/>
            <circle cx="20" cy="14" r="4" fill="white"/>
          </svg>
        </div>
        <h1>好物购买系统</h1>
        <p>商家入驻申请</p>
      </div>
      <div class="register-card">
        <h2>申请入驻</h2>
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label>登录账号 <span class="required">*</span></label>
            <input v-model="form.username" type="text" placeholder="请输入登录账号（4-20位）" required minlength="4" maxlength="20" />
          </div>
          <div class="form-group">
            <label>登录密码 <span class="required">*</span></label>
            <input v-model="form.password" type="password" placeholder="请输入密码（6-20位）" required minlength="6" maxlength="20" />
          </div>
          <div class="form-group">
            <label>确认密码 <span class="required">*</span></label>
            <input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" required />
          </div>
          <div class="form-group">
            <label>店铺名称 <span class="required">*</span></label>
            <input v-model="form.shopName" type="text" placeholder="请输入店铺名称" required maxlength="100" />
          </div>
          <div class="form-group">
            <label>手机号 <span class="required">*</span></label>
            <input v-model="form.phone" type="tel" placeholder="请输入手机号" required pattern="^1[3-9]\d{9}$" />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="form.email" type="email" placeholder="请输入邮箱（选填）" />
          </div>
          <div class="form-group">
            <label>店铺地址</label>
            <input v-model="form.shopAddress" type="text" placeholder="请输入店铺地址（选填）" maxlength="200" />
          </div>
          <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
          <button type="submit" class="btn-register" :disabled="loading">
            {{ loading ? '提交中...' : '提交申请' }}
          </button>
        </form>
        <div class="register-footer">
          <a href="#" @click.prevent="$router.push('/merchant/login')">已有账号？立即登录</a>
          <span>|</span>
          <a href="#" @click.prevent="$router.push('/')">返回首页</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { merchantRegister } from '@/api'

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  shopName: '',
  phone: '',
  email: '',
  shopAddress: ''
})

const handleRegister = async () => {
  errorMsg.value = ''
  if (form.value.password !== form.value.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }
  loading.value = true
  try {
    const { confirmPassword, ...submitData } = form.value
    const res = await merchantRegister(submitData)
    if (res.code === 200) {
      ElMessage.success('申请提交成功，请等待平台审核后登录')
      router.push('/merchant/login')
    } else {
      errorMsg.value = res.message || '注册失败，请重试'
    }
  } catch (e) {
    errorMsg.value = '注册失败，请重试'
    console.error('商家注册失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  padding: 24px 0;
}
.register-container { text-align: center; width: 100%; max-width: 460px; padding: 24px; }
.register-brand { margin-bottom: 28px; }
.brand-icon { display: flex; justify-content: center; margin-bottom: 12px; }
.brand-icon svg { width: 52px; height: 52px; }
.register-brand h1 { font-size: 24px; font-weight: 700; color: #1c1917; margin: 0 0 4px; }
.register-brand p { color: #f97316; font-size: 14px; font-weight: 500; margin: 0; }
.register-card { background: white; border-radius: 16px; padding: 32px 32px 28px; box-shadow: 0 4px 24px rgba(0,0,0,0.08); }
.register-card h2 { font-size: 20px; font-weight: 600; color: #1c1917; margin: 0 0 20px; }
.form-group { text-align: left; margin-bottom: 14px; }
.form-group label { display: block; font-size: 13px; color: #78716c; margin-bottom: 5px; font-weight: 500; }
.required { color: #ef4444; }
.form-group input { width: 100%; padding: 10px 14px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 14px; color: #1c1917; outline: none; transition: border-color 0.2s; box-sizing: border-box; }
.form-group input:focus { border-color: #f97316; }
.error-msg { color: #ef4444; font-size: 13px; margin-bottom: 12px; text-align: left; padding: 8px 12px; background: #fef2f2; border-radius: 6px; }
.btn-register { width: 100%; padding: 12px; background: #f97316; color: white; border: none; border-radius: 8px; font-size: 15px; font-weight: 600; cursor: pointer; transition: background 0.2s; margin-top: 4px; }
.btn-register:hover { background: #ea580c; }
.btn-register:disabled { background: #fdba74; cursor: not-allowed; }
.register-footer { margin-top: 18px; font-size: 13px; color: #a8a29e; display: flex; justify-content: center; gap: 12px; }
.register-footer a { color: #f97316; text-decoration: none; }
.register-footer a:hover { text-decoration: underline; }
</style>
