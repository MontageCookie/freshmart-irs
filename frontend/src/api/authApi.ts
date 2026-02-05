import { apiRequest } from './apiClient'

export interface LoginResponse {
  token: string
  tokenType: string
  expiresIn: number
  userId: number
  username: string
  roles: string[]
}

export interface MeResponse {
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

export function login(username: string, password: string) {
  return apiRequest<LoginResponse>(
    {
      method: 'POST',
      url: '/api/v1/auth/login',
      data: { username, password },
    },
    { silent: true },
  )
}

export function getMe() {
  return apiRequest<MeResponse>({
    method: 'GET',
    url: '/api/v1/auth/me',
  })
}

export function register(params: { username: string; password: string; phone?: string | null; email?: string | null }) {
  return apiRequest<IdResponse>(
    {
      method: 'POST',
      url: '/api/v1/auth/register',
      data: params,
    },
    { silent: true },
  )
}

export function logout() {
  return apiRequest<null>({
    method: 'POST',
    url: '/api/v1/auth/logout',
  })
}
