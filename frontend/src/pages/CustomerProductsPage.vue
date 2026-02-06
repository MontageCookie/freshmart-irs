<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { listProducts, type ProductListItemResponse } from '@/api/productsApi'
import { ApiError } from '@/api/types'

const router = useRouter()

const loading = ref(false)
const errorText = ref<string | null>(null)

const page = ref(1)
const size = ref(10)
const total = ref(0)
const items = ref<ProductListItemResponse[]>([])

const keyword = ref('')

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '加载失败'
}

async function load() {
  loading.value = true
  errorText.value = null
  try {
    const resp = await listProducts({
      page: page.value,
      size: size.value,
      keyword: keyword.value.trim() ? keyword.value.trim() : undefined,
    })
    items.value = resp.items
    total.value = resp.total
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    loading.value = false
  }
}

async function applyFilters() {
  page.value = 1
  await load()
}

async function onPageChange(p: number) {
  page.value = p
  await load()
}

async function openDetail(row: ProductListItemResponse) {
  await router.push(`/c/products/${row.id}`)
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="head">
      <h2 class="title">商品列表</h2>
      <div class="subtitle">匿名可浏览，点击进入详情</div>
    </div>

    <el-card>
      <div class="filters">
        <el-input
          v-model="keyword"
          clearable
          placeholder="名称关键字"
          @keydown.enter.prevent="applyFilters"
        />
        <el-button type="primary" :loading="loading" @click="applyFilters">搜索</el-button>
        <el-button :loading="loading" @click="load">刷新</el-button>
      </div>

      <el-alert
        v-if="errorText"
        class="alert"
        type="error"
        show-icon
        title="加载失败"
        :description="errorText"
      />

      <el-table v-else v-loading="loading" :data="items" class="table" @row-click="openDetail">
        <el-table-column prop="id" label="id" width="90" />
        <el-table-column prop="sku" label="sku" min-width="160" />
        <el-table-column prop="name" label="name" min-width="160" />
        <el-table-column prop="unit" label="unit" width="90" />
        <el-table-column prop="price" label="price" width="120" />
        <el-table-column prop="availableQty" label="availableQty" width="130" />
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :current-page="page"
          :page-size="size"
          :total="total"
          @current-change="onPageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.page {
  padding: 18px;
  max-width: 1100px;
  margin: 0 auto;
}

.head {
  margin-bottom: 12px;
}

.title {
  margin: 0;
  font-size: 18px;
}

.subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.55);
}

.filters {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
}

.alert {
  margin-bottom: 12px;
  white-space: pre-wrap;
}

.table {
  width: 100%;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
