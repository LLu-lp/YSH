<template>
  <div class="admin-login-page">
    <div class="login-container">
      <div class="login-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="40" height="40" rx="10" fill="#1c1917"/>
            <path d="M10 28l4-12h12l4 12H10z" fill="white" opacity="0.9"/>
            <circle cx="20" cy="14" r="4" fill="white"/>
          </svg>
        </div>
        <h1>好物购买系统</h1>
        <p>管理员控制台</p>
      </div>
      <div class="login-card">
        <h2>管理员登录</h2>
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label>账号</label>
            <input v-model="form.username" type="text" placeholder="请输入管理员账号" required />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input v-model="form.password" type="password" placeholder="请输入密码" required />
          </div>
          <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
          <button type="submit" class="btn-login" :disabled="loading">
            {{ loading ? '登录中...' : '立即登录' }}
          </button>
        </form>
        <div class="login-footer">
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
import { adminLogin } from '@/api'

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')
const form = ref({ username: '', password: '' })

const handleLogin = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await adminLogin(form.value)
    if (res.code === 200 && res.data) {
      localStorage.setItem('admin_token', res.data.token)
      localStorage.setItem('admin_info', JSON.stringify(res.data.adminInfo))
      ElMessage.success('登录成功')
      router.push('/admin/dashboard')
    } else {
      errorMsg.value = res.message || '登录失败'
    }
  } catch (e) {
    errorMsg.value = '账号或密码错误'
    console.error('登录失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1c1917 0%, #292524 60%, #3b3634 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
.login-container { text-align: center; width: 100%; max-width: 420px; padding: 24px; }
.login-brand { margin-bottom: 32px; }
.brand-icon { display: flex; justify-content: center; margin-bottom: 12px; }
.brand-icon svg { width: 56px; height: 56px; }
.login-brand h1 { font-size: 26px; font-weight: 700; color: white; margin: 0 0 4px; }
.login-brand p { color: #a8a29e; font-size: 14px; font-weight: 500; margin: 0; }
.login-card { background: white; border-radius: 16px; padding: 36px 32px; }
.login-card h2 { font-size: 20px; font-weight: 600; color: #1c1917; margin: 0 0 24px; }
.form-group { text-align: left; margin-bottom: 16px; }
.form-group label { display: block; font-size: 13px; color: #78716c; margin-bottom: 6px; font-weight: 500; }
.form-group input { width: 100%; padding: 10px 14px; border: 1.5px solid #e7e5e4; border-radius: 8px; font-size: 14px; color: #1c1917; outline: none; transition: border-color 0.2s; box-sizing: border-box; }
.form-group input:focus { border-color: #1c1917; }
.error-msg { color: #ef4444; font-size: 13px; margin-bottom: 12px; text-align: left; }
.btn-login { width: 100%; padding: 12px; background: #1c1917; color: white; border: none; border-radius: 8px; font-size: 15px; font-weight: 600; cursor: pointer; transition: background 0.2s; margin-top: 4px; }
.btn-login:hover { background: #292524; }
.btn-login:disabled { background: #78716c; cursor: not-allowed; }
.login-footer { margin-top: 20px; font-size: 13px; color: #a8a29e; }
.login-footer a { color: #78716c; text-decoration: none; }
.login-footer a:hover { text-decoration: underline; }
</style>
