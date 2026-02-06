<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'

import { createProduct, type ProductStatus, type ProductUpsertRequest } from '@/modules/catalog/api/productsApi'
import { ApiError } from '@/api/types'
import { refreshMe, useAuthState } from '@/auth/authStore'
import { pushToast } from '@/ui/toast'

const router = useRouter()
const authState = useAuthState()

const isStoreManager = computed(() => Boolean(authState.me?.roles?.includes('STORE_MANAGER')))

const loading = ref(false)
const errorText = ref<string | null>(null)

const formRef = ref<FormInstance>()
const form = reactive<Required<ProductUpsertRequest>>({
  sku: '',
  name: '',
  unit: '',
  price: 0,
  safetyStockQty: 0,
  status: 'ON_SALE',
})

const rules: FormRules = {
  sku: [{ required: true, message: 'sku 不能为空', trigger: 'blur' }],
  name: [{ required: true, message: 'name 不能为空', trigger: 'blur' }],
  unit: [{ required: true, message: 'unit 不能为空', trigger: 'blur' }],
  price: [{ required: true, message: 'price 不能为空', trigger: 'change' }],
  safetyStockQty: [{ type: 'number', min: 0, message: 'safetyStockQty 必须 >= 0', trigger: 'change' }],
}

const statusOptions: { label: ProductStatus; value: ProductStatus }[] = [
  { label: 'ON_SALE', value: 'ON_SALE' },
  { label: 'OFF_SALE', value: 'OFF_SALE' },
]

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '提交失败'
}

async function back() {
  await router.push('/manager/products')
}

async function submit() {
  errorText.value = null
  if (!authState.me) {
    await refreshMe()
  }
  if (!isStoreManager.value) {
    errorText.value = '无权限'
    return
  }

  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) {
    return
  }

  loading.value = true
  try {
    const resp = await createProduct({
      sku: form.sku.trim(),
      name: form.name.trim(),
      unit: form.unit.trim(),
      price: form.price,
      safetyStockQty: form.safetyStockQty,
      status: form.status,
    })
    pushToast('创建成功', 'info')
    await router.replace(`/manager/products/${resp.id}`)
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="head">
      <h2 class="title">创建商品</h2>
      <div class="actions">
        <el-button @click="back">返回列表</el-button>
        <el-button type="primary" :loading="loading" :disabled="!isStoreManager" @click="submit">提交</el-button>
      </div>
    </div>

    <el-card>
      <el-alert
        v-if="errorText"
        class="alert"
        type="error"
        show-icon
        title="提交失败"
        :description="errorText"
      />

      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px" class="form">
        <el-form-item label="sku" prop="sku">
          <el-input v-model="form.sku" autocomplete="off" />
        </el-form-item>
        <el-form-item label="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="unit" prop="unit">
          <el-input v-model="form.unit" autocomplete="off" />
        </el-form-item>
        <el-form-item label="price" prop="price">
          <el-input-number v-model="form.price" :min="0" :step="0.1" :precision="2" />
        </el-form-item>
        <el-form-item label="safetyStockQty" prop="safetyStockQty">
          <el-input-number v-model="form.safetyStockQty" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="status" prop="status">
          <el-select v-model="form.status" class="select">
            <el-option v-for="x in statusOptions" :key="x.value" :label="x.label" :value="x.value" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.page {
  padding: 18px;
  max-width: 900px;
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

.form {
  margin-top: 6px;
}

.select {
  width: 220px;
}
</style>
