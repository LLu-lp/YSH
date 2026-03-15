<template>
  <div class="coupon-manage">
    <div class="page-header">
      <h2>优惠券管理</h2>
      <el-button type="primary" @click="openCreate">+ 创建优惠券</el-button>
    </div>

    <el-card>
      <el-table :data="couponList" v-loading="loading" stripe>
        <el-table-column prop="name" label="优惠券名称" min-width="160" />
        <el-table-column prop="value" label="优惠金额" width="100">
          <template #default="{ row }">¥{{ row.value }}</template>
        </el-table-column>
        <el-table-column prop="minAmount" label="使用门槛" width="110">
          <template #default="{ row }">满¥{{ row.minAmount }}</template>
        </el-table-column>
        <el-table-column label="有效期" min-width="200">
          <template #default="{ row }">{{ formatDate(row.startTime) }} ~ {{ formatDate(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="剩余/总量" width="110">
          <template #default="{ row }">{{ row.remainingCount }} / {{ row.totalCount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建优惠券弹窗 -->
    <el-dialog v-model="createVisible" title="创建平台优惠券" width="500px">
      <el-form :model="createForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入优惠券名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="优惠金额" prop="value">
          <el-input-number v-model="createForm.value" :min="0.01" :precision="2" :step="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="使用门槛" prop="minAmount">
          <el-input-number v-model="createForm.minAmount" :min="0" :precision="2" :step="10" style="width:100%" />
          <div class="form-tip">0 表示无门槛</div>
        </el-form-item>
        <el-form-item label="有效期" prop="dateRange">
          <el-date-picker
            v-model="createForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="发放总量" prop="totalCount">
          <el-input-number v-model="createForm.totalCount" :min="1" :step="100" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleCreate">确认创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminGetCoupons, adminCreateCoupon, adminDeleteCoupon } from '@/api'

const loading = ref(false)
const submitting = ref(false)
const createVisible = ref(false)
const couponList = ref([])
const formRef = ref()

const createForm = reactive({
  name: '',
  value: 5,
  minAmount: 0,
  dateRange: null,
  totalCount: 100
})

const rules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  value: [{ required: true, message: '请输入优惠金额', trigger: 'change' }],
  dateRange: [{ required: true, message: '请选择有效期', trigger: 'change' }],
  totalCount: [{ required: true, message: '请输入发放数量', trigger: 'change' }]
}

const formatDate = (t) => t ? new Date(t).toLocaleDateString('zh-CN') : '-'

const loadCoupons = async () => {
  loading.value = true
  try {
    const res = await adminGetCoupons()
    if (res.code === 200 && res.data) {
      couponList.value = Array.isArray(res.data) ? res.data : res.data.content || []
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  createForm.name = ''
  createForm.value = 5
  createForm.minAmount = 0
  createForm.dateRange = null
  createForm.totalCount = 100
  createVisible.value = true
}

const handleCreate = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const [startTime, endTime] = createForm.dateRange
    const res = await adminCreateCoupon({
      name: createForm.name,
      type: 1,
      value: createForm.value,
      minAmount: createForm.minAmount,
      startTime,
      endTime,
      totalCount: createForm.totalCount,
      remainingCount: createForm.totalCount
    })
    if (res.code === 200) {
      ElMessage.success('优惠券创建成功')
      createVisible.value = false
      loadCoupons()
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除优惠券「${row.name}」吗？`, '提示', { type: 'warning' })
    const res = await adminDeleteCoupon(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadCoupons()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => loadCoupons())
</script>

<style scoped>
.coupon-manage { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #1f2937; }
.form-tip { font-size: 12px; color: #9ca3af; margin-top: 4px; }
</style>
