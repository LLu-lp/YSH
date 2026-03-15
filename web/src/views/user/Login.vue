<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-left">
        <div class="brand">
          <div class="logo-icon">好</div>
          <h1>好物购买系统</h1>
        </div>
        <p class="slogan">发现好物，品质生活</p>
        <div class="features">
          <div class="feature-item"><el-icon><CircleCheck /></el-icon> 海量优质商品</div>
          <div class="feature-item"><el-icon><CircleCheck /></el-icon> 安全支付保障</div>
          <div class="feature-item"><el-icon><CircleCheck /></el-icon> 快速物流配送</div>
        </div>
      </div>
      <div class="auth-right">
        <div class="auth-form">
          <h2>用户登录</h2>
          <el-form ref="formRef" :model="form" :rules="rules" size="large">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名/手机号" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
            </el-form-item>
            <div class="form-footer">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <el-link type="primary">忘记密码？</el-link>
            </div>
            <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleLogin">
              立即登录
            </el-button>
          </el-form>
          <el-divider>还没有账号？</el-divider>
          <el-button size="large" style="width:100%" @click="$router.push('/user/register')">
            免费注册
          </el-button>
          <div class="back-link">
            <el-link @click="$router.push('/')">← 返回首页</el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user.js'
import { userLogin } from '@/api'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码不少于6位', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await userLogin({ username: form.username, password: form.password })
    if (res.code === 200) {
      userStore.setUser(res.data.token, res.data.user || res.data.userInfo)
      ElMessage.success('登录成功！')
      router.push('/user/home')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (e) {
    console.error('登录失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
}
.auth-container {
  display: flex;
  width: 900px;
  min-height: 560px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0,0,0,0.12);
}
.auth-left {
  flex: 1;
  background: linear-gradient(135deg, #ff6b35, #ff8c5a);
  padding: 60px 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #fff;
}
.brand { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; }
.logo-icon {
  width: 48px; height: 48px; background: rgba(255,255,255,0.25);
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  font-size: 22px; font-weight: 800; color: #fff;
}
.brand h1 { font-size: 22px; font-weight: 700; }
.slogan { font-size: 28px; font-weight: 700; margin-bottom: 40px; line-height: 1.4; }
.features { display: flex; flex-direction: column; gap: 16px; }
.feature-item { display: flex; align-items: center; gap: 10px; font-size: 15px; opacity: 0.9; }
.auth-right {
  flex: 1;
  background: #fff;
  padding: 60px 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.auth-form { width: 100%; }
.auth-form h2 { font-size: 26px; font-weight: 700; color: #333; margin-bottom: 32px; }
.form-footer { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.back-link { text-align: center; margin-top: 20px; }
</style>
