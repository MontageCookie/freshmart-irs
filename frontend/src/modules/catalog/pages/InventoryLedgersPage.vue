<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { listInventoryLedgers, type InventoryLedgerListItemResponse } from '@/modules/catalog/api/inventoryApi'
import { ApiError } from '@/api/types'

const loading = ref(false)
const errorText = ref<string | null>(null)

const page = ref(1)
const size = ref(10)
const total = ref(0)
const items = ref<InventoryLedgerListItemResponse[]>([])

const productId = ref<number | null>(null)
const batchId = ref<number | null>(null)
const changeType = ref<string | null>(null)
const sourceType = ref<string | null>(null)
const eventRange = ref<[Date, Date] | null>(null)

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '加载失败'
}

function toIso(d: Date | null | undefined): string | undefined {
  if (!d) return undefined
  const t = d.getTime()
  if (!Number.isFinite(t)) return undefined
  return new Date(t).toISOString()
}

async function load() {
  loading.value = true
  errorText.value = null
  try {
    const from = eventRange.value?.[0]
    const to = eventRange.value?.[1]
    const resp = await listInventoryLedgers({
      page: page.value,
      size: size.value,
      productId: productId.value ?? undefined,
      batchId: batchId.value ?? undefined,
      changeType: changeType.value ? changeType.value : undefined,
      sourceType: sourceType.value ? sourceType.value : undefined,
      eventFrom: toIso(from),
      eventTo: toIso(to),
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

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="head">
      <h2 class="title">库存流水</h2>
      <el-button :loading="loading" @click="load">刷新</el-button>
    </div>

    <el-card>
      <div class="filters">
        <el-input-number v-model="productId" :min="1" :controls="false" placeholder="productId" class="num" />
        <el-input-number v-model="batchId" :min="1" :controls="false" placeholder="batchId" class="num" />
        <el-select v-model="changeType" clearable placeholder="changeType" class="sel">
          <el-option label="STOCK_IN" value="STOCK_IN" />
          <el-option label="SALE_POS" value="SALE_POS" />
          <el-option label="SALE_ONLINE" value="SALE_ONLINE" />
          <el-option label="ADJUST" value="ADJUST" />
        </el-select>
        <el-input v-model="sourceType" clearable placeholder="sourceType" class="sel" />
        <el-date-picker
          v-model="eventRange"
          type="datetimerange"
          start-placeholder="eventFrom"
          end-placeholder="eventTo"
          range-separator="~"
          class="range"
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

      <el-table v-else v-loading="loading" :data="items" class="table">
        <el-table-column prop="id" label="id" width="90" />
        <el-table-column prop="productId" label="productId" width="110" />
        <el-table-column prop="inventoryBatchId" label="batchId" width="110" />
        <el-table-column prop="changeType" label="changeType" width="130" />
        <el-table-column prop="changeQty" label="changeQty" width="110" />
        <el-table-column prop="qtyAfter" label="qtyAfter" width="110" />
        <el-table-column prop="sourceType" label="sourceType" min-width="160" />
        <el-table-column prop="sourceId" label="sourceId" width="110" />
        <el-table-column prop="eventTime" label="eventTime" min-width="200" />
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
  max-width: 1400px;
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
  grid-template-columns: 160px 160px 170px 200px 1fr auto;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
}

.num,
.sel,
.range {
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
