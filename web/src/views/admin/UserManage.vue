<template>
  <div class="user-manage">
    <div class="page-header">
      <h2>用户管理</h2>
    </div>

    <div class="search-bar">
      <input v-model="searchKeyword" placeholder="搜索用户名/手机号/邮箱" class="search-input" @keyup.enter="loadUsers" />
      <select v-model="statusFilter" class="filter-select">
        <option value="">全部状态</option>
        <option value="1">正常</option>
        <option value="0">禁用</option>
      </select>
      <button @click="loadUsers" class="btn-primary">搜索</button>
      <button @click="handleReset" class="btn-outline">重置</button>
    </div>

    <div class="table-card" v-loading="loading">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>头像</th>
            <th>用户名</th>
            <th>手机号</th>
            <th>邮箱</th>
            <th>注册时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in userList" :key="user.id">
            <td>{{ user.id }}</td>
            <td>
              <img :src="convertAvatarUrl(user.avatar)" class="avatar" alt="头像" />
            </td>
            <td>{{ user.username }}</td>
            <td>{{ user.phone || '-' }}</td>
            <td>{{ user.email || '-' }}</td>
            <td>{{ formatDateTime(user.createTime) }}</td>
            <td>
              <span :class="['status-badge', user.status === 1 ? 'active' : 'disabled']">
                {{ user.status === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>
              <button @click="viewUser(user)" class="btn-link">查看</button>
              <button @click="toggleStatus(user)" :class="user.status === 1 ? 'btn-link danger' : 'btn-link success'">
                {{ user.status === 1 ? '禁用' : '启用' }}
              </button>
            </td>
          </tr>
          <tr v-if="userList.length === 0 && !loading">
            <td colspan="8" class="empty">暂无用户数据</td>
          </tr>
        </tbody>
      </table>

      <div class="pagination">
        <button :disabled="page <= 1" @click="page--; loadUsers()">上一页</button>
        <span>第 {{ page }} / {{ totalPages }} 页，共 {{ total }} 条</span>
        <button :disabled="page >= totalPages" @click="page++; loadUsers()">下一页</button>
      </div>
    </div>

    <div v-if="showDetail" class="modal-overlay" @click.self="showDetail = false">
      <div class="modal">
        <div class="modal-header">
          <h3>用户详情</h3>
          <button @click="showDetail = false" class="close-btn">×</button>
        </div>
        <div class="modal-body" v-if="currentUser">
          <div class="detail-item"><label>用户名：</label><span>{{ currentUser.username }}</span></div>
          <div class="detail-item"><label>手机号：</label><span>{{ currentUser.phone || '-' }}</span></div>
          <div class="detail-item"><label>邮箱：</label><span>{{ currentUser.email || '-' }}</span></div>
          <div class="detail-item"><label>注册时间：</label><span>{{ formatDateTime(currentUser.createTime) }}</span></div>
          <div class="detail-item"><label>最后登录：</label><span>{{ formatDateTime(currentUser.lastLoginTime) }}</span></div>
          <div class="detail-item">
            <label>状态：</label>
            <span :class="['status-badge', currentUser.status === 1 ? 'active' : 'disabled']">
              {{ currentUser.status === 1 ? '正常' : '禁用' }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGetUsers, freezeUser } from '@/api'

const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const showDetail = ref(false)
const currentUser = ref(null)

const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)
const userList = ref([])

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

// 转换头像 URL
const convertAvatarUrl = (avatarUrl) => {
  if (!avatarUrl) return '/placeholder.svg?height=36&width=36'
  // 如果是完整 URL，直接返回
  if (avatarUrl.startsWith('http')) return avatarUrl
  // 如果是 /uploads/avatar/xxx.jpg 格式，转换为 /api/files/avatar/xxx.jpg
  if (avatarUrl.startsWith('/uploads/avatar/')) {
    const filename = avatarUrl.replace('/uploads/avatar/', '')
    return `/api/files/avatar/${filename}`
  }
  return avatarUrl
}

const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize,
      keyword: searchKeyword.value || undefined,
      status: statusFilter.value !== '' ? parseInt(statusFilter.value) : undefined
    }
    const res = await adminGetUsers(params)
    if (res.code === 200 && res.data) {
      if (res.data.content) {
        userList.value = res.data.content
        total.value = res.data.totalElements || 0
      } else if (Array.isArray(res.data)) {
        userList.value = res.data
        total.value = res.data.length
      }
    }
  } catch (error) {
    console.error('加载用户失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchKeyword.value = ''
  statusFilter.value = ''
  page.value = 1
  loadUsers()
}

const viewUser = (user) => {
  currentUser.value = user
  showDetail.value = true
}

const toggleStatus = async (user) => {
  const action = user.status === 1 ? '禁用' : '启用'
  if (!confirm(`确定要${action}该用户吗？`)) return
  try {
    await freezeUser(user.id, { status: user.status === 1 ? 0 : 1 })
    user.status = user.status === 1 ? 0 : 1
    ElMessage.success(`${action}成功`)
  } catch (error) {
    console.error('操作失败:', error)
  }
}

onMounted(() => loadUsers())
</script>

<style scoped>
.user-manage { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #1f2937; }
.search-bar { display: flex; gap: 10px; margin-bottom: 20px; align-items: center; flex-wrap: wrap; }
.search-input { padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; width: 240px; font-size: 14px; }
.filter-select { padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; }
.btn-primary { padding: 8px 18px; background: #1c1917; color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; }
.btn-outline { padding: 8px 18px; background: #fff; color: #374151; border: 1px solid #d1d5db; border-radius: 6px; cursor: pointer; font-size: 14px; }
.table-card { background: #fff; border-radius: 10px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.07); }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th { background: #f9fafb; padding: 12px 10px; text-align: left; color: #6b7280; font-weight: 500; border-bottom: 1px solid #e5e7eb; }
.data-table td { padding: 12px 10px; border-bottom: 1px solid #f3f4f6; color: #374151; }
.avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; }
.status-badge { padding: 2px 10px; border-radius: 20px; font-size: 12px; }
.status-badge.active { background: #d1fae5; color: #065f46; }
.status-badge.disabled { background: #fee2e2; color: #991b1b; }
.btn-link { background: none; border: none; cursor: pointer; font-size: 13px; color: #1c1917; margin-right: 8px; padding: 0; }
.btn-link.danger { color: #dc2626; }
.btn-link.success { color: #16a34a; }
.empty { text-align: center; padding: 40px; color: #9ca3af; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 12px; margin-top: 16px; font-size: 14px; color: #6b7280; }
.pagination button { padding: 6px 14px; border: 1px solid #d1d5db; border-radius: 6px; background: #fff; cursor: pointer; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: #fff; border-radius: 12px; width: 440px; overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #e5e7eb; }
.modal-header h3 { font-size: 16px; font-weight: 600; color: #1f2937; }
.close-btn { background: none; border: none; font-size: 22px; cursor: pointer; color: #9ca3af; }
.modal-body { padding: 20px; }
.detail-item { display: flex; align-items: center; padding: 8px 0; font-size: 14px; border-bottom: 1px solid #f3f4f6; }
.detail-item label { width: 90px; color: #6b7280; }
.detail-item span { color: #1f2937; }
</style>
