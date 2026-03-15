<template>
  <div class="address-page">
    <div class="page-header">
      <h2>收货地址</h2>
      <button @click="openAdd" class="btn-primary">+ 新增地址</button>
    </div>

    <div class="address-list" v-loading="loading">
      <div v-for="addr in addressList" :key="addr.id" class="address-card" :class="{ default: addr.isDefault === 1 || addr.isDefault === true }">
        <div class="addr-info">
          <div class="addr-top">
            <span class="receiver">{{ addr.receiver }}</span>
            <span class="phone">{{ addr.phone }}</span>
            <span v-if="addr.isDefault === 1 || addr.isDefault === true" class="default-tag">默认</span>
          </div>
          <div class="addr-detail">{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detail }}</div>
        </div>
        <div class="addr-actions">
          <button v-if="addr.isDefault !== 1 && addr.isDefault !== true" @click="setDefault(addr)" class="btn-link">设为默认</button>
          <button @click="editAddr(addr)" class="btn-link">编辑</button>
          <button @click="deleteAddr(addr)" class="btn-link danger">删除</button>
        </div>
      </div>
      <div v-if="addressList.length === 0 && !loading" class="empty-state">
        <p>暂无收货地址，请添加</p>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ isEdit ? '编辑地址' : '新增地址' }}</h3>
          <button @click="showModal = false" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-item">
              <label>收货人</label>
              <input v-model="form.receiver" placeholder="请输入收货人姓名" class="input" />
            </div>
            <div class="form-item">
              <label>手机号</label>
              <input v-model="form.phone" placeholder="请输入手机号" class="input" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>省份</label>
              <select v-model="form.province" class="input" @change="onProvinceChange">
                <option value="">请选择省份</option>
                <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
              </select>
            </div>
            <div class="form-item">
              <label>城市</label>
              <select v-model="form.city" class="input" @change="onCityChange" :disabled="!form.province">
                <option value="">请选择城市</option>
                <option v-for="c in cities" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>
            <div class="form-item">
              <label>区/县</label>
              <select v-model="form.district" class="input" :disabled="!form.city">
                <option value="">请选择区/县</option>
                <option v-for="d in districts" :key="d" :value="d">{{ d }}</option>
              </select>
            </div>
          </div>
          <div class="form-item full">
            <label>详细地址</label>
            <input v-model="form.detail" placeholder="街道、楼栋、门牌号等" class="input" />
          </div>
          <div class="form-item full">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.isDefault" />
              设为默认地址
            </label>
          </div>
          <div class="modal-footer">
            <button @click="saveAddress" class="btn-primary" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
            <button @click="showModal = false" class="btn-outline">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getAddresses, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api'
import { getProvinces, getCities, getDistricts } from '@/data/regions'

const loading = ref(false)
const saving = ref(false)
const addressList = ref([])

const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)

const form = reactive({ receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: false })

// 地区数据
const provinces = computed(() => getProvinces())
const cities = computed(() => getCities(form.province))
const districts = computed(() => getDistricts(form.province, form.city))

const loadAddresses = async () => {
  loading.value = true
  try {
    const res = await getAddresses()
    if (res.code === 200 && res.data) {
      addressList.value = res.data.map(a => ({
        ...a,
        isDefault: a.isDefault === 1 || a.isDefault === true
      }))
    }
  } catch (error) {
    console.error('加载地址失败:', error)
  } finally {
    loading.value = false
  }
}

function openAdd() {
  isEdit.value = false
  editId.value = null
  Object.assign(form, { receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: false })
  showModal.value = true
}

function editAddr(addr) {
  isEdit.value = true
  editId.value = addr.id
  Object.assign(form, { ...addr, isDefault: addr.isDefault === 1 || addr.isDefault === true })
  showModal.value = true
}

// 省份变化时，重置城市和区县
const onProvinceChange = () => {
  form.city = ''
  form.district = ''
}

// 城市变化时，重置区县
const onCityChange = () => {
  form.district = ''
}

const saveAddress = async () => {
  if (!form.receiver || !form.phone || !form.province || !form.city || !form.district || !form.detail) {
    ElMessage.warning('请填写完整信息')
    return
  }
  saving.value = true
  try {
    const data = { ...form, isDefault: form.isDefault ? 1 : 0 }
    if (isEdit.value) {
      await updateAddress(editId.value, data)
      ElMessage.success('修改成功')
    } else {
      await addAddress(data)
      ElMessage.success('添加成功')
    }
    showModal.value = false
    loadAddresses()
  } catch (error) {
    console.error('保存地址失败:', error)
  } finally {
    saving.value = false
  }
}

const setDefault = async (addr) => {
  try {
    await setDefaultAddress(addr.id)
    ElMessage.success('已设为默认地址')
    loadAddresses()
  } catch (error) {
    console.error('设置默认地址失败:', error)
  }
}

const deleteAddr = async (addr) => {
  if (!confirm('确定删除该地址吗？')) return
  try {
    await deleteAddress(addr.id)
    ElMessage.success('删除成功')
    loadAddresses()
  } catch (error) {
    console.error('删除地址失败:', error)
  }
}

onMounted(() => loadAddresses())
</script>

<style scoped>
.address-page { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #1f2937; }
.btn-primary { padding: 8px 18px; background: #ff6b35; color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-outline { padding: 8px 18px; background: #fff; color: #374151; border: 1px solid #d1d5db; border-radius: 6px; cursor: pointer; font-size: 14px; }
.address-list { display: flex; flex-direction: column; gap: 12px; }
.address-card { background: #fff; border-radius: 10px; padding: 16px 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.07); display: flex; justify-content: space-between; align-items: center; border: 2px solid transparent; transition: border-color 0.2s; }
.address-card.default { border-color: #ff6b35; }
.addr-top { display: flex; align-items: center; gap: 12px; margin-bottom: 6px; }
.receiver { font-weight: 600; font-size: 15px; color: #1f2937; }
.phone { font-size: 14px; color: #6b7280; }
.default-tag { background: #ff6b35; color: #fff; font-size: 11px; padding: 2px 8px; border-radius: 10px; }
.addr-detail { font-size: 14px; color: #6b7280; }
.addr-actions { display: flex; gap: 12px; flex-shrink: 0; }
.btn-link { background: none; border: none; cursor: pointer; font-size: 13px; color: #ff6b35; padding: 0; }
.btn-link.danger { color: #dc2626; }
.empty-state { text-align: center; padding: 60px; color: #9ca3af; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: #fff; border-radius: 12px; width: 520px; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #e5e7eb; }
.modal-header h3 { font-size: 16px; font-weight: 600; color: #1f2937; }
.close-btn { background: none; border: none; font-size: 22px; cursor: pointer; color: #9ca3af; }
.modal-body { padding: 20px; }
.form-row { display: flex; gap: 12px; margin-bottom: 14px; }
.form-item { flex: 1; }
.form-item.full { margin-bottom: 14px; }
.form-item label { display: block; font-size: 13px; color: #6b7280; margin-bottom: 6px; font-weight: 500; }
.input { width: 100%; padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; box-sizing: border-box; }
.input:disabled { background-color: #f3f4f6; cursor: not-allowed; color: #9ca3af; }
.checkbox-label { display: flex; align-items: center; gap: 8px; font-size: 14px; color: #374151; cursor: pointer; }
.modal-footer { display: flex; gap: 10px; justify-content: flex-end; margin-top: 16px; }
</style>
