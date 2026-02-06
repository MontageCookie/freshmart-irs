<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { getProduct, type ProductDetailResponse } from '@/api/productsApi'
import { ApiError } from '@/api/types'

const route = useRoute()
const router = useRouter()

const id = computed(() => Number(route.params.id))

const loading = ref(false)
const errorText = ref<string | null>(null)
const data = ref<ProductDetailResponse | null>(null)

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '加载失败'
}

async function load() {
  if (!Number.isFinite(id.value) || id.value <= 0) {
    errorText.value = '参数错误'
    return
  }
  loading.value = true
  errorText.value = null
  try {
    data.value = await getProduct(id.value)
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    loading.value = false
  }
}

async function back() {
  await router.push('/c/products')
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="head">
      <h2 class="title">商品详情</h2>
      <el-button @click="back">返回列表</el-button>
    </div>

    <el-card v-loading="loading">
      <el-alert
        v-if="errorText"
        class="alert"
        type="error"
        show-icon
        title="加载失败"
        :description="errorText"
      />

      <template v-else>
        <el-descriptions v-if="data" :column="1" border>
          <el-descriptions-item label="id">{{ data.id }}</el-descriptions-item>
          <el-descriptions-item label="sku">{{ data.sku }}</el-descriptions-item>
          <el-descriptions-item label="name">{{ data.name }}</el-descriptions-item>
          <el-descriptions-item label="unit">{{ data.unit }}</el-descriptions-item>
          <el-descriptions-item label="price">{{ data.price }}</el-descriptions-item>
          <el-descriptions-item label="availableQty">{{ data.availableQty }}</el-descriptions-item>
          <el-descriptions-item label="safetyStockQty">{{ data.safetyStockQty }}</el-descriptions-item>
          <el-descriptions-item label="status">{{ data.status }}</el-descriptions-item>
        </el-descriptions>

        <div v-else class="empty">暂无数据</div>
      </template>
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

.alert {
  white-space: pre-wrap;
}

.empty {
  padding: 12px 0;
  color: rgba(0, 0, 0, 0.55);
}
</style>
