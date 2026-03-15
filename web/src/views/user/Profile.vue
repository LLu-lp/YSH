<template>
  <div class="profile-page">
    <div class="profile-layout">
      <div class="profile-sidebar">
        <div class="avatar-section">
          <el-avatar :size="80" :src="form.avatar || ''">{{ form.username?.charAt(0) }}</el-avatar>
          <p class="username">{{ form.username }}</p>
          <p class="user-id">ID: {{ form.id }}</p>
        </div>
        <el-menu :default-active="activeMenu" @select="activeMenu = $event">
          <el-menu-item index="info"><el-icon><User /></el-icon> 基本信息</el-menu-item>
          <el-menu-item index="security"><el-icon><Lock /></el-icon> 账号安全</el-menu-item>
          <el-menu-item index="address" @click="$router.push('/user/address')"><el-icon><Location /></el-icon> 收货地址</el-menu-item>
          <el-menu-item index="favorites" @click="$router.push('/user/favorites')"><el-icon><Star /></el-icon> 我的收藏</el-menu-item>
        </el-menu>
      </div>

      <div class="profile-content">
        <div v-if="activeMenu === 'info'" class="content-card">
          <h3 class="card-title">基本信息</h3>
          <el-form :model="form" ref="formRef" :rules="rules" label-width="100px" size="large">
            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                action="/api/upload/avatar"
                :show-file-list="false"
                :before-upload="beforeUpload"
                :on-success="handleAvatarSuccess"
                :on-error="handleAvatarError"
                :headers="uploadHeaders"
              >
                <el-avatar :size="80" :src="form.avatar || ''">{{ form.username?.charAt(0) }}</el-avatar>
                <div class="upload-tip">点击更换头像</div>
              </el-upload>
            </el-form-item>
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-if="activeMenu === 'security'" class="content-card">
          <h3 class="card-title">账号安全</h3>
          <div class="security-list">
            <div class="security-item">
              <div class="security-info">
                <p class="security-name">登录密码</p>
                <p class="security-desc">建议使用包含字母、数字的复杂密码</p>
              </div>
              <el-button @click="showPwdDialog = true">修改密码</el-button>
            </div>
            <div class="security-item">
              <div class="security-info">
                <p class="security-name">手机绑定</p>
                <p class="security-desc">当前绑定：{{ form.phone || '未绑定' }}</p>
              </div>
              <el-button>修改手机</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="showPwdDialog" title="修改密码" width="480px" @close="resetPwdForm">
      <el-form :model="pwdForm" ref="pwdFormRef" :rules="pwdRules" label-width="100px">
        <el-form-item label="原密码" prop="old">
          <el-input v-model="pwdForm.old" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="new">
          <el-input v-model="pwdForm.new" type="password" show-password placeholder="请输入新密码（至少6位）" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirm">
          <el-input v-model="pwdForm.confirm" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePwd">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Location, Star } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user.js'
import { getUserInfo, updateUserInfo, changePassword } from '@/api'

const router = useRouter()
const userStore = useUserStore()
const activeMenu = ref('info')
const saving = ref(false)
const showPwdDialog = ref(false)
const formRef = ref()
const pwdFormRef = ref()

// 上传头像时的请求头
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${localStorage.getItem('user_token') || ''}`
}))

const form = reactive({
  id: null,
  username: '',
  phone: '',
  email: '',
  avatar: ''
})

const pwdForm = reactive({ old: '', new: '', confirm: '' })

const validateNewPwd = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入新密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (value === pwdForm.old) {
    callback(new Error('新密码不能与原密码相同'))
  } else {
    callback()
  }
}

const validateConfirmPwd = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认新密码'))
  } else if (value !== pwdForm.new) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

const pwdRules = {
  old: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  new: [{ validator: validateNewPwd, trigger: 'blur' }],
  confirm: [{ validator: validateConfirmPwd, trigger: 'blur' }]
}

const resetPwdForm = () => {
  pwdForm.old = ''
  pwdForm.new = ''
  pwdForm.confirm = ''
  pwdFormRef.value?.clearValidate()
}

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 200 && res.data) {
      Object.assign(form, {
        id: res.data.id,
        username: res.data.username || '',
        phone: res.data.phone || '',
        email: res.data.email || '',
        avatar: res.data.avatar ? convertAvatarUrl(res.data.avatar) : ''
      })
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

// 转换头像 URL，使用新的文件下载端点
const convertAvatarUrl = (avatarUrl) => {
  if (!avatarUrl) return ''
  // 如果是 /uploads/avatar/xxx.jpg 格式，转换为 /api/files/avatar/xxx.jpg
  if (avatarUrl.startsWith('/uploads/avatar/')) {
    const filename = avatarUrl.replace('/uploads/avatar/', '')
    return `/api/files/avatar/${filename}`
  }
  return avatarUrl
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
  return false
  }
  return true
}

const handleAvatarSuccess = async (response) => {
  if (response.code === 200 && response.data?.url) {
    // 保存原始 URL 到数据库，但显示时使用转换后的 URL
    const originalUrl = response.data.url
    form.avatar = convertAvatarUrl(originalUrl)
    ElMessage.success('头像上传成功，正在保存...')
    
    // 自动保存头像到数据库
    try {
      const res = await updateUserInfo({
        username: form.username,
        phone: form.phone,
        email: form.email,
        avatar: originalUrl  // 保存原始 URL
      })
      if (res.code === 200) {
        userStore.setUser(userStore.token, { ...userStore.userInfo, ...form })
        ElMessage.success('头像已保存')
      } else {
        ElMessage.error(res.message || '保存失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '保存失败')
    }
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const handleAvatarError = () => {
  ElMessage.error('头像上传失败')
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    const res = await updateUserInfo({
      username: form.username,
      phone: form.phone,
      email: form.email,
      avatar: form.avatar
    })
    if (res.code === 200) {
    userStore.setUser(userStore.token, { ...userStore.userInfo, ...form })
    ElMessage.success('保存成功！')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const handleChangePwd = async () => {
  try {
    await pwdFormRef.value.validate()
    const res = await changePassword({ oldPassword: pwdForm.old, newPassword: pwdForm.new })
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      showPwdDialog.value = false
      resetPwdForm()
      userStore.logout()
      router.push('/user/login')
    } else {
      ElMessage.error(res.message || '密码修改失败')
    }
  } catch (error) {
    if (error.message && !error.message.includes('Validation failed')) {
      ElMessage.error(error.message || '修改密码失败')
    }
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page { min-height: 60vh; }
.profile-layout { display: flex; gap: 24px; }
.profile-sidebar { width: 220px; flex-shrink: 0; background: #fff; border-radius: 12px; overflow: hidden; }
.avatar-section { padding: 32px 20px 20px; text-align: center; background: #fff8f5; }
.username { font-size: 16px; font-weight: 700; color: #333; margin: 12px 0 4px; }
.user-id { font-size: 12px; color: #999; }
.profile-content { flex: 1; }
.content-card { background: #fff; border-radius: 12px; padding: 32px; }
.card-title { font-size: 18px; font-weight: 700; color: #333; margin-bottom: 28px; padding-bottom: 16px; border-bottom: 1px solid #f0f0f0; }
.avatar-uploader { position: relative; cursor: pointer; }
.avatar-uploader:hover .upload-tip { opacity: 1; }
.upload-tip { 
  position: absolute; 
  bottom: -24px; 
  left: 0; 
  font-size: 12px; 
  color: #ff6b35; 
  opacity: 0.7;
  transition: opacity 0.3s;
}
.avatar-uploader :deep(.el-avatar) { 
  transition: all 0.3s; 
  border: 2px solid #f0f0f0;
}
.avatar-uploader:hover :deep(.el-avatar) { 
  border-color: #ff6b35; 
  transform: scale(1.05);
}
.security-list { display: flex; flex-direction: column; gap: 0; }
.security-item { 
  display: flex; 
  align-items: center; 
  justify-content: space-between; 
  padding: 20px 0; 
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.3s;
}
.security-item:hover { background: #fafaf9; }
.security-item:last-child { border-bottom: none; }
.security-info { flex: 1; }
.security-name { font-size: 15px; font-weight: 600; color: #333; margin-bottom: 4px; }
.security-desc { font-size: 13px; color: #999; }
</style>
