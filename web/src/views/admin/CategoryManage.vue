<template>
  <div class="category-manage">
    <div class="page-header">
      <h1>分类管理</h1>
      <button class="btn-primary" @click="showAddForm = true">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        添加分类
      </button>
    </div>
    
    <div class="card">
      <div class="card-header">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchKeyword" 
            placeholder="搜索分类名称" 
            class="search-input"
          />
          <button class="search-btn">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
          </button>
        </div>
      </div>
      <div class="card-body">
        <div class="category-tree">
          <div 
            v-for="category in filteredCategories" 
            :key="category.id"
            class="category-item"
          >
            <div class="category-info">
              <span class="category-name">{{ category.name }}</span>
              <span class="category-status" :class="{ active: category.status === 1 }">
                {{ category.status === 1 ? '启用' : '禁用' }}
              </span>
            </div>
            <div class="category-actions">
              <button class="btn-secondary" @click="editCategory(category)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                编辑
              </button>
              <button class="btn-danger" @click="deleteCategory(category.id)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
                删除
              </button>
            </div>
          </div>
          <div v-if="filteredCategories.length === 0" class="empty-state">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            <p>暂无分类数据</p>
          </div>
        </div>
      </div>
      <div class="card-footer">
        <div class="pagination">
          <button class="page-btn" :disabled="currentPage === 1">上一页</button>
          <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
          <button class="page-btn" :disabled="currentPage === totalPages">下一页</button>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑分类弹窗 -->
    <div v-if="showAddForm || showEditForm" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditForm ? '编辑分类' : '添加分类' }}</h3>
          <button class="close-btn" @click="closeModal">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <label>分类名称</label>
              <input 
                type="text" 
                v-model="formData.name" 
                class="form-control"
                placeholder="请输入分类名称"
                required
              />
            </div>
            <div class="form-group">
              <label>父分类</label>
              <select v-model="formData.parentId" class="form-control">
                <option value="0">顶级分类</option>
                <option 
                  v-for="category in categories" 
                  :key="category.id"
                  :value="category.id"
                >
                  {{ category.name }}
                </option>
              </select>
            </div>
            <div class="form-group">
              <label>分类状态</label>
              <div class="status-toggle">
                <input 
                  type="checkbox" 
                  id="status" 
                  v-model="formData.status"
                />
                <label for="status">{{ formData.status ? '启用' : '禁用' }}</label>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" class="btn-secondary" @click="closeModal">取消</button>
              <button type="submit" class="btn-primary">{{ showEditForm ? '保存修改' : '添加分类' }}</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGetCategories, adminAddCategory, adminUpdateCategory, adminDeleteCategory } from '@/api'

const categories = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const showAddForm = ref(false)
const showEditForm = ref(false)
const loading = ref(false)
const formData = ref({
  id: '',
  name: '',
  parentId: 0,
  status: 1
})

const loadCategories = async () => {
  loading.value = true
  try {
    const res = await adminGetCategories()
    if (res.code === 200 && res.data) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  } finally {
    loading.value = false
  }
}

const filteredCategories = computed(() => {
  if (!searchKeyword.value) {
    return categories.value
  }
  return categories.value.filter(category => 
    category.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const totalPages = computed(() => {
  return Math.ceil(filteredCategories.value.length / 10)
})

const editCategory = (category) => {
  formData.value = { ...category }
  showEditForm.value = true
}

const deleteCategory = async (id) => {
  if (!confirm('确定要删除这个分类吗？')) return
  try {
    await adminDeleteCategory(id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    console.error('删除分类失败:', error)
  }
}

const handleSubmit = async () => {
  try {
    const data = {
      name: formData.value.name,
      parentId: formData.value.parentId || 0,
      status: formData.value.status ? 1 : 0
    }
    if (showEditForm.value) {
      await adminUpdateCategory(formData.value.id, data)
      ElMessage.success('修改成功')
    } else {
      await adminAddCategory(data)
      ElMessage.success('添加成功')
    }
    closeModal()
    loadCategories()
  } catch (error) {
    console.error('保存分类失败:', error)
  }
}

const closeModal = () => {
  showAddForm.value = false
  showEditForm.value = false
  formData.value = {
    id: '',
    name: '',
    parentId: 0,
    status: 1
  }
}

onMounted(() => loadCategories())
</script>

<style scoped>
.category-manage { padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 20px; font-weight: 600; margin: 0; color: #1c1917; }
.btn-primary, .btn-secondary, .btn-danger { display: flex; align-items: center; gap: 6px; padding: 8px 16px; border: none; border-radius: 6px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.15s; }
.btn-primary { background: #007bff; color: white; }
.btn-primary:hover { background: #0069d9; }
.btn-secondary { background: #6c757d; color: white; }
.btn-secondary:hover { background: #5a6268; }
.btn-danger { background: #dc3545; color: white; }
.btn-danger:hover { background: #c82333; }
.btn-primary svg, .btn-secondary svg, .btn-danger svg { width: 16px; height: 16px; }
.card { background: white; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); overflow: hidden; }
.card-header { padding: 16px 20px; border-bottom: 1px solid #e5e7eb; }
.search-box { display: flex; gap: 8px; }
.search-input { flex: 1; padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; }
.search-input:focus { outline: none; border-color: #007bff; box-shadow: 0 0 0 3px rgba(0,123,255,0.1); }
.search-btn { background: #f3f4f6; border: 1px solid #d1d5db; border-radius: 6px; padding: 0 12px; cursor: pointer; }
.search-btn:hover { background: #e5e7eb; }
.search-btn svg { width: 16px; height: 16px; color: #6b7280; }
.card-body { padding: 20px; }
.category-tree { display: flex; flex-direction: column; gap: 12px; }
.category-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border: 1px solid #e5e7eb; border-radius: 6px; transition: all 0.15s; }
.category-item:hover { border-color: #007bff; box-shadow: 0 0 0 3px rgba(0,123,255,0.1); }
.category-info { display: flex; align-items: center; gap: 16px; }
.category-name { font-size: 14px; font-weight: 500; color: #1c1917; }
.category-status { font-size: 12px; padding: 2px 8px; border-radius: 10px; font-weight: 500; }
.category-status.active { background: #d1fae5; color: #065f46; }
.category-status:not(.active) { background: #fee2e2; color: #b91c1c; }
.category-actions { display: flex; gap: 8px; }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 48px 0; color: #6b7280; }
.empty-state svg { width: 48px; height: 48px; margin-bottom: 16px; opacity: 0.5; }
.empty-state p { margin: 0; font-size: 14px; }
.card-footer { padding: 16px 20px; border-top: 1px solid #e5e7eb; background: #f9fafb; }
.pagination { display: flex; align-items: center; gap: 16px; justify-content: center; }
.page-btn { padding: 6px 12px; border: 1px solid #d1d5db; border-radius: 4px; background: white; font-size: 14px; cursor: pointer; }
.page-btn:hover:not(:disabled) { background: #f3f4f6; }
.page-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.page-info { font-size: 14px; color: #6b7280; }
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: white; border-radius: 8px; width: 90%; max-width: 500px; max-height: 90vh; overflow-y: auto; }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #e5e7eb; }
.modal-header h3 { font-size: 16px; font-weight: 600; margin: 0; color: #1c1917; }
.close-btn { background: none; border: none; cursor: pointer; padding: 4px; border-radius: 4px; }
.close-btn:hover { background: #f3f4f6; }
.close-btn svg { width: 16px; height: 16px; color: #6b7280; }
.modal-body { padding: 20px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; font-size: 14px; font-weight: 500; color: #1c1917; margin-bottom: 6px; }
.form-control { width: 100%; padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; }
.form-control:focus { outline: none; border-color: #007bff; box-shadow: 0 0 0 3px rgba(0,123,255,0.1); }
.status-toggle { display: flex; align-items: center; gap: 8px; }
.status-toggle input[type="checkbox"] { width: 16px; height: 16px; }
.form-actions { display: flex; gap: 8px; justify-content: flex-end; margin-top: 24px; }
</style>