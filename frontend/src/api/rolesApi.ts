import { apiRequest } from './apiClient'

export interface RoleResponse {
  id: number
  code: string
  name: string
  enabled: boolean
}

export function listRoles() {
  return apiRequest<RoleResponse[]>({
    method: 'GET',
    url: '/api/v1/roles',
  })
}

