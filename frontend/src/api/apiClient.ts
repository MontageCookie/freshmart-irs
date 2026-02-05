import axios, { type AxiosError, type AxiosRequestConfig } from 'axios'

import { getToken } from '@/auth/token'
import { ApiError, type ApiResponse, isAuthInvalidError } from './types'

type ErrorNotifier = (error: ApiError) => void
type AuthInvalidHandler = (error: ApiError) => void

let errorNotifier: ErrorNotifier | null = null
let authInvalidHandler: AuthInvalidHandler | null = null

export function setErrorNotifier(fn: ErrorNotifier | null) {
  errorNotifier = fn
}

export function setAuthInvalidHandler(fn: AuthInvalidHandler | null) {
  authInvalidHandler = fn
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const EXCLUDED_AUTH_PATHS = new Set<string>(['/api/v1/auth/login', '/api/v1/auth/register'])

function toPathname(url: string): string {
  try {
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return new URL(url).pathname
    }
  } catch {
    return url
  }
  return url
}

function shouldAttachAuth(url: string | undefined): boolean {
  if (!url) {
    return true
  }
  const pathname = toPathname(url.startsWith('/') ? url : `/${url}`)
  return !EXCLUDED_AUTH_PATHS.has(pathname)
}

function redactSensitive(data: unknown): unknown {
  if (!data || typeof data !== 'object') {
    return data
  }
  if (Array.isArray(data)) {
    return data.map((x) => redactSensitive(x))
  }
  const src = data as Record<string, unknown>
  const dst: Record<string, unknown> = {}
  for (const [k, v] of Object.entries(src)) {
    const key = k.toLowerCase()
    if (key.includes('password')) {
      dst[k] = '[REDACTED]'
      continue
    }
    if (key === 'token' || key === 'access_token' || key === 'authorization') {
      dst[k] = '[REDACTED]'
      continue
    }
    dst[k] = redactSensitive(v)
  }
  return dst
}

function redactAuthHeader(headers: unknown): unknown {
  if (!headers || typeof headers !== 'object') {
    return headers
  }
  const src = headers as Record<string, unknown>
  const dst: Record<string, unknown> = { ...src }
  for (const k of Object.keys(dst)) {
    if (k.toLowerCase() === 'authorization') {
      dst[k] = '[REDACTED]'
    }
  }
  return dst
}

const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15_000,
})

http.interceptors.request.use((config) => {
  const token = getToken()
  const url = config.url
  if (token && shouldAttachAuth(url)) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }

  if (import.meta.env.DEV) {
    const method = (config.method || 'get').toUpperCase()
    const fullUrl = `${config.baseURL || ''}${url || ''}`
    console.debug('[HTTP]', method, fullUrl, {
      params: redactSensitive(config.params),
      data: redactSensitive(config.data),
      headers: redactAuthHeader(config.headers),
    })
  }

  return config
})

function normalizeToApiError(err: unknown): ApiError {
  if (err instanceof ApiError) {
    return err
  }

  if (axios.isAxiosError(err)) {
    const axiosErr = err as AxiosError
    const httpStatus = axiosErr.response?.status
    const data = axiosErr.response?.data as Partial<ApiResponse<unknown>> | undefined

    if (data && typeof data.code === 'number' && typeof data.message === 'string') {
      return new ApiError({
        code: data.code,
        message: data.message,
        traceId: typeof data.traceId === 'string' ? data.traceId : undefined,
        httpStatus,
      })
    }

    if (httpStatus) {
      return new ApiError({
        message: `HTTP ${httpStatus}`,
        httpStatus,
      })
    }

    return new ApiError({
      message: '网络错误或服务器不可达',
    })
  }

  return new ApiError({
    message: '未知错误',
  })
}

/**
 * 统一请求入口：解析 ApiResponse，失败时抛 ApiError
 */
export async function apiRequest<T>(
  config: AxiosRequestConfig,
  options?: { silent?: boolean },
): Promise<T> {
  try {
    const response = await http.request<ApiResponse<T>>(config)
    const body = response.data
    if (!body || typeof body.code !== 'number') {
      throw new ApiError({ message: '响应格式不正确' })
    }
    if (body.code !== 0) {
      throw new ApiError({
        code: body.code,
        message: body.message || '请求失败',
        traceId: body.traceId,
        httpStatus: response.status,
      })
    }
    return body.data as T
  } catch (e) {
    const apiError = normalizeToApiError(e)

    if (isAuthInvalidError(apiError) && authInvalidHandler) {
      authInvalidHandler(apiError)
    }
    if (!options?.silent && errorNotifier) {
      errorNotifier(apiError)
    }

    throw apiError
  }
}
