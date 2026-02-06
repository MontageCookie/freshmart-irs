export interface ApiResponse<T> {
  code: number
  message: string
  data: T | null
  traceId: string
}

export interface PageResponse<T> {
  page: number
  size: number
  total: number
  items: T[]
}

export class ApiError extends Error {
  code?: number
  traceId?: string
  httpStatus?: number

  constructor(params: { message: string; code?: number; traceId?: string; httpStatus?: number }) {
    super(params.message)
    this.name = 'ApiError'
    this.code = params.code
    this.traceId = params.traceId
    this.httpStatus = params.httpStatus
  }
}

export function isAuthInvalidError(err: unknown): err is ApiError {
  if (!(err instanceof ApiError)) {
    return false
  }
  return err.code === 40100 || err.httpStatus === 401
}

export function isForbiddenError(err: unknown): err is ApiError {
  if (!(err instanceof ApiError)) {
    return false
  }
  return err.code === 40300 || err.httpStatus === 403
}
