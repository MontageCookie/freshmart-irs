<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

import {
  getInventorySnapshot,
  type InventoryBatchSnapshotResponse,
  type InventorySnapshotItemResponse,
} from '@/api/inventoryApi'
import { ApiError } from '@/api/types'

const loading = ref(false)
const errorText = ref<string | null>(null)

const page = ref(1)
const size = ref(10)
const total = ref(0)
const items = ref<InventorySnapshotItemResponse[]>([])

const keyword = ref('')

const batchState = reactive<Record<number, { loading: boolean; errorText: string | null; batches: InventoryBatchSnapshotResponse[] }>>(
  {},
)

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
    const resp = await getInventorySnapshot({
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

async function ensureBatches(productId: number) {
  if (batchState[productId]?.loading) return
  if (batchState[productId] && batchState[productId].batches.length > 0) return

  batchState[productId] = batchState[productId] || { loading: false, errorText: null, batches: [] }
  batchState[productId].loading = true
  batchState[productId].errorText = null
  try {
    const resp = await getInventorySnapshot({ productId, page: 1, size: 1 })
    const first = resp.items?.[0]
    batchState[productId].batches = first?.batches ? first.batches : []
  } catch (e) {
    batchState[productId].errorText = formatError(e)
  } finally {
    batchState[productId].loading = false
  }
}

async function onExpandChange(row: InventorySnapshotItemResponse, expanded: boolean) {
  if (!expanded) return
  await ensureBatches(row.productId)
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="head">
      <h2 class="title">库存快照</h2>
      <el-button :loading="loading" @click="load">刷新</el-button>
    </div>

    <el-card>
      <div class="filters">
        <el-input
          v-model="keyword"
          clearable
          placeholder="商品名称 / SKU 关键字"
          @keydown.enter.prevent="applyFilters"
        />
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

      <el-table v-else v-loading="loading" :data="items" row-key="productId" class="table" @expand-change="onExpandChange">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="expand">
              <div v-if="batchState[row.productId]?.loading" class="hint">加载批次中...</div>
              <el-alert
                v-else-if="batchState[row.productId]?.errorText"
                class="alert"
                type="error"
                show-icon
                title="加载批次失败"
                :description="batchState[row.productId]?.errorText || ''"
              />
              <el-table
                v-else
                :data="batchState[row.productId]?.batches || []"
                size="small"
                class="batch-table"
              >
                <el-table-column prop="id" label="batchId" width="100" />
                <el-table-column prop="batchNo" label="batchNo" min-width="160" />
                <el-table-column prop="expiryDate" label="expiryDate" width="130" />
                <el-table-column prop="qtyInitial" label="qtyInitial" width="120" />
                <el-table-column prop="qtyAvailable" label="qtyAvailable" width="130" />
                <el-table-column prop="status" label="status" width="120" />
                <el-table-column prop="receivedAt" label="receivedAt" min-width="200" />
              </el-table>
              <div v-if="!(batchState[row.productId]?.batches || []).length && !batchState[row.productId]?.loading" class="hint">
                暂无批次明细
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="productId" label="productId" width="110" />
        <el-table-column prop="sku" label="sku" min-width="150" />
        <el-table-column prop="name" label="name" min-width="160" />
        <el-table-column prop="unit" label="unit" width="90" />
        <el-table-column prop="status" label="status" width="120" />
        <el-table-column prop="safetyStockQty" label="safetyStockQty" width="150" />
        <el-table-column prop="availableQty" label="availableQty" width="130" />
        <el-table-column prop="totalQty" label="totalQty" width="110" />
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
  max-width: 1300px;
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

.filters {
  display: grid;
  grid-template-columns: 1fr auto;
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

.expand {
  padding: 10px 10px 4px;
}

.batch-table {
  width: 100%;
}

.hint {
  color: rgba(0, 0, 0, 0.55);
  font-size: 13px;
  padding: 6px 0;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
