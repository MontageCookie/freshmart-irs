<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

import { getProduct, offSaleProduct, updateProduct, type ProductDetailResponse, type ProductStatus } from '@/api/productsApi'
import { ApiError } from '@/api/types'
import { refreshMe, useAuthState } from '@/auth/authStore'
import { pushToast } from '@/ui/toast'

const route = useRoute()
const router = useRouter()
const authState = useAuthState()

const id = computed(() => Number(route.params.id))
const isStoreManager = computed(() => Boolean(authState.me?.roles?.includes('STORE_MANAGER')))

const loading = ref(false)
const saving = ref(false)
const errorText = ref<string | null>(null)

const formRef = ref<FormInstance>()

const detail = ref<ProductDetailResponse | null>(null)
const form = reactive<{
  sku: string
  name: string
  unit: string
  price: number
  safetyStockQty: number
  status: ProductStatus
}>({
  sku: '',
  name: '',
  unit: '',
  price: 0,
  safetyStockQty: 0,
  status: 'ON_SALE',
})

const rules: FormRules = {
  price: [{ required: true, message: 'price 不能为空', trigger: 'change' }],
  safetyStockQty: [{ type: 'number', min: 0, message: 'safetyStockQty 必须 >= 0', trigger: 'change' }],
}

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '操作失败'
}

function syncFormFromDetail(d: ProductDetailResponse) {
  form.sku = d.sku
  form.name = d.name
  form.unit = d.unit
  form.price = d.price
  form.safetyStockQty = d.safetyStockQty
  form.status = d.status
}

async function load() {
  if (!Number.isFinite(id.value) || id.value <= 0) {
    errorText.value = '参数错误'
    return
  }
  loading.value = true
  errorText.value = null
  try {
    if (!authState.me) {
      await refreshMe()
    }
    detail.value = await getProduct(id.value)
    syncFormFromDetail(detail.value)
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    loading.value = false
  }
}

async function back() {
  await router.push('/products')
}

async function save() {
  errorText.value = null
  if (!detail.value) return
  if (!isStoreManager.value) {
    errorText.value = '无权限'
    return
  }

  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) return

  saving.value = true
  try {
    await updateProduct(detail.value.id, {
      sku: form.sku,
      name: form.name,
      unit: form.unit,
      price: form.price,
      safetyStockQty: form.safetyStockQty,
      status: form.status,
    })
    pushToast('保存成功', 'info')
    await load()
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    saving.value = false
  }
}

async function offSale() {
  errorText.value = null
  if (!detail.value) return
  if (!isStoreManager.value) {
    errorText.value = '无权限'
    return
  }
  if (detail.value.status === 'OFF_SALE') {
    return
  }
  try {
    await ElMessageBox.confirm(`确认下架商品：${detail.value.name}（${detail.value.sku}）？`, '下架确认', {
      type: 'warning',
      confirmButtonText: '下架',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  saving.value = true
  try {
    await offSaleProduct(detail.value.id)
    pushToast('下架成功', 'info')
    await load()
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="head">
      <h2 class="title">商品详情</h2>
      <div class="actions">
        <el-button @click="back">返回列表</el-button>
        <el-button v-if="isStoreManager" type="primary" :loading="saving" :disabled="loading" @click="save">
          保存
        </el-button>
        <el-button
          v-if="isStoreManager"
          type="danger"
          plain
          :loading="saving"
          :disabled="loading || detail?.status === 'OFF_SALE'"
          @click="offSale"
        >
          下架
        </el-button>
      </div>
    </div>

    <el-card v-loading="loading">
      <el-alert
        v-if="errorText"
        class="alert"
        type="error"
        show-icon
        title="操作失败"
        :description="errorText"
      />

      <template v-else>
        <el-descriptions v-if="detail" :column="2" border class="meta">
          <el-descriptions-item label="id">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="availableQty">{{ detail.availableQty }}</el-descriptions-item>
        </el-descriptions>

        <el-form ref="formRef" :model="form" :rules="rules" label-width="140px" class="form">
          <el-form-item label="sku">
            <el-input v-model="form.sku" disabled />
          </el-form-item>
          <el-form-item label="name">
            <el-input v-model="form.name" disabled />
          </el-form-item>
          <el-form-item label="unit">
            <el-input v-model="form.unit" disabled />
          </el-form-item>
          <el-form-item label="price" prop="price">
            <el-input-number v-model="form.price" :min="0" :step="0.1" :precision="2" :disabled="!isStoreManager" />
          </el-form-item>
          <el-form-item label="safetyStockQty" prop="safetyStockQty">
            <el-input-number v-model="form.safetyStockQty" :min="0" :step="1" :disabled="!isStoreManager" />
          </el-form-item>
          <el-form-item label="status">
            <el-select v-model="form.status" class="select" :disabled="!isStoreManager">
              <el-option label="ON_SALE" value="ON_SALE" />
              <el-option label="OFF_SALE" value="OFF_SALE" />
            </el-select>
          </el-form-item>
        </el-form>
      </template>
    </el-card>
  </div>
</template>

<style scoped>
.page {
  padding: 18px;
  max-width: 1000px;
  margin: 0 auto;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.title {
  margin: 0;
  font-size: 18px;
}

.actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.alert {
  margin-bottom: 12px;
  white-space: pre-wrap;
}

.meta {
  margin-bottom: 12px;
}

.form {
  margin-top: 6px;
}

.select {
  width: 220px;
}
</style>
