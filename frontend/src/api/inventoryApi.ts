import { apiRequest } from './apiClient'
import type { PageResponse } from './types'
import type { ProductStatus } from './productsApi'

export type InventoryBatchStatus = 'AVAILABLE' | 'BLOCKED' | 'EXPIRED'

export interface InventoryBatchSnapshotResponse {
  id: number
  batchNo: string
  expiryDate: string
  qtyInitial: number
  qtyAvailable: number
  status: InventoryBatchStatus
  receivedAt: string
}

export interface InventorySnapshotItemResponse {
  productId: number
  sku: string
  name: string
  unit: string
  price: number
  status: ProductStatus
  safetyStockQty: number
  availableQty: number
  totalQty: number
  batches: InventoryBatchSnapshotResponse[] | null
}

export interface InventoryLedgerListItemResponse {
  id: number
  productId: number
  inventoryBatchId: number
  changeType: string
  changeQty: number
  qtyAfter: number
  sourceType: string
  sourceId: number
  eventTime: string
}

export function getInventorySnapshot(params?: {
  productId?: number
  keyword?: string
  page?: number
  size?: number
  sort?: string[]
}) {
  return apiRequest<PageResponse<InventorySnapshotItemResponse>>({
    method: 'GET',
    url: '/api/v1/inventory/snapshot',
    params: {
      productId: params?.productId,
      keyword: params?.keyword,
      page: params?.page ?? 1,
      size: params?.size ?? 10,
      sort: params?.sort,
    },
  })
}

export function listInventoryLedgers(params?: {
  productId?: number
  batchId?: number
  changeType?: string
  sourceType?: string
  eventFrom?: string
  eventTo?: string
  page?: number
  size?: number
  sort?: string[]
}) {
  return apiRequest<PageResponse<InventoryLedgerListItemResponse>>({
    method: 'GET',
    url: '/api/v1/inventory/ledgers',
    params: {
      productId: params?.productId,
      batchId: params?.batchId,
      changeType: params?.changeType,
      sourceType: params?.sourceType,
      eventFrom: params?.eventFrom,
      eventTo: params?.eventTo,
      page: params?.page ?? 1,
      size: params?.size ?? 10,
      sort: params?.sort,
    },
  })
}
