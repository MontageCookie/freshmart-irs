import { apiRequest } from '@/api/apiClient'
import type { PageResponse } from '@/api/types'

export interface UserListItemResponse {
  id: number
  username: string
  phone: string | null
  email: string | null
  status: 'ENABLED' | 'DISABLED'
  createdAt: string
}

export function listUsers(params?: {
  page?: number
  size?: number
  keyword?: string
  status?: 'ENABLED' | 'DISABLED'
  sort?: string[]
}) {
  return apiRequest<PageResponse<UserListItemResponse>>({
    method: 'GET',
    url: '/api/v1/users',
    params: {
      page: params?.page ?? 1,
      size: params?.size ?? 10,
      keyword: params?.keyword,
      status: params?.status,
      sort: params?.sort,
    },
  })
}

export interface UserDetailResponse {
  id: number
  username: string
  phone: string | null
  email: string | null
  status: 'ENABLED' | 'DISABLED'
  roles: string[]
}

export interface IdResponse {
  id: number
}

export interface UserCreateRequest {
  username: string
  password: string
  phone?: string | null
  email?: string | null
  roleIds: number[]
}

export function createUser(req: UserCreateRequest) {
  return apiRequest<IdResponse>({
    method: 'POST',
    url: '/api/v1/users',
    data: req,
  })
}

export function getUser(id: number) {
  return apiRequest<UserDetailResponse>({
    method: 'GET',
    url: `/api/v1/users/${id}`,
  })
}

export interface UserUpdateRequest {
  username?: string | null
  phone?: string | null
  email?: string | null
  currentPassword?: string | null
  newPassword?: string | null
}

export function updateUser(id: number, req: UserUpdateRequest) {
  return apiRequest<IdResponse>({
    method: 'PATCH',
    url: `/api/v1/users/${id}`,
    data: req,
  })
}

export function disableUser(id: number) {
  return apiRequest<IdResponse>({
    method: 'DELETE',
    url: `/api/v1/users/${id}`,
  })
}

export interface UserRolesUpdateRequest {
  roleIds: number[]
}

export function updateUserRoles(id: number, req: UserRolesUpdateRequest) {
  return apiRequest<IdResponse>({
    method: 'PUT',
    url: `/api/v1/users/${id}/roles`,
    data: req,
  })
}
