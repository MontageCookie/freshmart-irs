import { apiRequest } from './apiClient'
import type { PageResponse } from './types'

export type ProductStatus = 'ON_SALE' | 'OFF_SALE'

export interface ProductListItemResponse {
  id: number
  sku: string
  name: string
  unit: string
  price: number
  status: ProductStatus
  availableQty: number
}

export interface ProductDetailResponse {
  id: number
  sku: string
  name: string
  unit: string
  price: number
  safetyStockQty: number
  status: ProductStatus
  availableQty: number
}

export interface ProductUpsertRequest {
  sku: string
  name: string
  unit: string
  price: number
  safetyStockQty?: number | null
  status?: ProductStatus | null
}

export interface IdResponse {
  id: number
}

export function listProducts(params?: {
  page?: number
  size?: number
  keyword?: string
  status?: ProductStatus
  sort?: string[]
}) {
  return apiRequest<PageResponse<ProductListItemResponse>>({
    method: 'GET',
    url: '/api/v1/products',
    params: {
      page: params?.page ?? 1,
      size: params?.size ?? 10,
      keyword: params?.keyword,
      status: params?.status,
      sort: params?.sort,
    },
  })
}

export function getProduct(id: number) {
  return apiRequest<ProductDetailResponse>({
    method: 'GET',
    url: `/api/v1/products/${id}`,
  })
}

export function createProduct(data: ProductUpsertRequest) {
  return apiRequest<IdResponse>({
    method: 'POST',
    url: '/api/v1/products',
    data,
  })
}

export function updateProduct(id: number, data: ProductUpsertRequest) {
  return apiRequest<IdResponse>({
    method: 'PUT',
    url: `/api/v1/products/${id}`,
    data,
  })
}

export function offSaleProduct(id: number) {
  return apiRequest<IdResponse>({
    method: 'DELETE',
    url: `/api/v1/products/${id}`,
  })
}
