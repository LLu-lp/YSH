<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-left">
        <div class="brand">
          <div class="logo-icon">好</div>
          <h1>好物购买系统</h1>
        </div>
        <p class="slogan">加入我们，<br/>开启好物之旅</p>
        <div class="features">
          <div class="feature-item"><el-icon><CircleCheck /></el-icon> 注册即享新人优惠券</div>
          <div class="feature-item"><el-icon><CircleCheck /></el-icon> 百万优质商品任您选</div>
          <div class="feature-item"><el-icon><CircleCheck /></el-icon> 7天无理由退换保障</div>
        </div>
      </div>
      <div class="auth-right">
        <div class="auth-form">
          <h2>创建账号</h2>
          <el-form ref="formRef" :model="form" :rules="rules" size="large">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名（4-16位字母或数字）" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" :prefix-icon="Phone" />
            </el-form-item>
            <el-form-item prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱（选填）" :prefix-icon="Message" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="请设置密码（6-20位）" :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item prop="agree">
              <el-checkbox v-model="form.agree">
                我已阅读并同意 <el-link type="primary">《用户协议》</el-link> 和 <el-link type="primary">《隐私政策》</el-link>
              </el-checkbox>
            </el-form-item>
            <el-button type="primary" size="large" style="width:100%;margin-top:8px" :loading="loading" @click="handleRegister">
              立即注册
            </el-button>
          </el-form>
          <div class="login-link">
            已有账号？<el-link type="primary" @click="$router.push('/user/login')">立即登录</el-link>
          </div>
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
import { User, Lock, Phone, Message } from '@element-plus/icons-vue'
import { userRegister } from '@/api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', phone: '', email: '', password: '', confirmPassword: '', agree: false })

const validatePass2 = (rule, value, callback) => {
  if (value !== form.password) callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 4, max: 16, message: '4-16位', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  password: [{ required: true, message: '请设置密码', trigger: 'blur' }, { min: 6, max: 20, message: '6-20位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validatePass2, trigger: 'blur' }],
  agree: [{ validator: (r, v, cb) => v ? cb() : cb(new Error('请同意用户协议')), trigger: 'change' }]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await userRegister({ 
      username: form.username, 
      phone: form.phone, 
      email: form.email, 
      password: form.password 
    })
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录！')
      router.push('/user/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (e) {
    console.error('注册失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page { min-height:100vh; background:#f8f9fa; display:flex; align-items:center; justify-content:center; }
.auth-container { display:flex; width:960px; min-height:620px; border-radius:20px; overflow:hidden; box-shadow:0 8px 40px rgba(0,0,0,0.12); }
.auth-left { flex:1; background:linear-gradient(135deg,#ff6b35,#ff8c5a); padding:60px 48px; display:flex; flex-direction:column; justify-content:center; color:#fff; }
.brand { display:flex; align-items:center; gap:12px; margin-bottom:24px; }
.logo-icon { width:48px; height:48px; background:rgba(255,255,255,0.25); border-radius:12px; display:flex; align-items:center; justify-content:center; font-size:22px; font-weight:800; color:#fff; }
.brand h1 { font-size:22px; font-weight:700; }
.slogan { font-size:26px; font-weight:700; margin-bottom:40px; line-height:1.5; }
.features { display:flex; flex-direction:column; gap:16px; }
.feature-item { display:flex; align-items:center; gap:10px; font-size:15px; opacity:0.9; }
.auth-right { flex:1.2; background:#fff; padding:60px 48px; display:flex; align-items:center; justify-content:center; }
.auth-form { width:100%; }
.auth-form h2 { font-size:26px; font-weight:700; color:#333; margin-bottom:28px; }
.login-link { text-align:center; margin-top:20px; color:#666; font-size:14px; }
.back-link { text-align:center; margin-top:12px; }
</style>
