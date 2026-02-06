<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

import { listProducts, offSaleProduct, type ProductListItemResponse, type ProductStatus } from '@/api/productsApi'
import { ApiError } from '@/api/types'
import { refreshMe, useAuthState } from '@/auth/authStore'
import { pushToast } from '@/ui/toast'

const router = useRouter()
const authState = useAuthState()

const isStoreManager = computed(() => Boolean(authState.me?.roles?.includes('STORE_MANAGER')))

const loading = ref(false)
const errorText = ref<string | null>(null)

const page = ref(1)
const size = ref(10)
const total = ref(0)
const items = ref<ProductListItemResponse[]>([])

const keyword = ref('')
const status = ref<ProductStatus>('ON_SALE')

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
    if (!authState.me) {
      await refreshMe()
    }
    const resp = await listProducts({
      page: page.value,
      size: size.value,
      keyword: keyword.value.trim() ? keyword.value.trim() : undefined,
      status: status.value,
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

async function goCreate() {
  await router.push('/products/new')
}

async function openDetail(row: ProductListItemResponse) {
  await router.push(`/products/${row.id}`)
}

async function offSale(row: ProductListItemResponse) {
  if (!isStoreManager.value) {
    return
  }
  if (row.status === 'OFF_SALE') {
    return
  }
  try {
    await ElMessageBox.confirm(`确认下架商品：${row.name}（${row.sku}）？`, '下架确认', {
      type: 'warning',
      confirmButtonText: '下架',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  try {
    await offSaleProduct(row.id)
    pushToast('下架成功', 'info')
    await load()
  } catch (e) {
    errorText.value = formatError(e)
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="head">
      <h2 class="title">商品管理</h2>
      <div class="actions">
        <el-button :disabled="!isStoreManager" type="primary" @click="goCreate">创建商品</el-button>
        <el-button :loading="loading" @click="load">刷新</el-button>
      </div>
    </div>

    <el-card>
      <div class="filters">
        <el-input
          v-model="keyword"
          clearable
          placeholder="名称关键字"
          @keydown.enter.prevent="applyFilters"
        />
        <el-select v-model="status" class="status" placeholder="状态">
          <el-option label="ON_SALE" value="ON_SALE" />
          <el-option label="OFF_SALE" value="OFF_SALE" />
        </el-select>
        <el-button type="primary" :loading="loading" @click="applyFilters">筛选</el-button>
      </div>

      <el-alert
        v-if="errorText"
        class="alert"
        type="error"
        show-icon
        title="加载失败"
        :description="errorText"
      />

      <el-table v-else v-loading="loading" :data="items" class="table">
        <el-table-column prop="id" label="id" width="90" />
        <el-table-column prop="sku" label="sku" min-width="150" />
        <el-table-column prop="name" label="name" min-width="160" />
        <el-table-column prop="unit" label="unit" width="90" />
        <el-table-column prop="price" label="price" width="120" />
        <el-table-column prop="availableQty" label="availableQty" width="130" />
        <el-table-column prop="status" label="status" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ON_SALE' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="ops" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button v-if="isStoreManager" link type="danger" :disabled="row.status === 'OFF_SALE'" @click="offSale(row)">
              下架
            </el-button>
          </template>
        </el-table-column>
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
  max-width: 1200px;
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

.filters {
  display: grid;
  grid-template-columns: 1fr 220px auto;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.status {
  width: 100%;
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
