import { computed, reactive } from 'vue'

import { getMe, type MeResponse } from '@/modules/security/api/authApi'
import { isAuthInvalidError } from '@/api/types'
import { clearToken } from './token'

const state = reactive<{
  me: MeResponse | null
  loadingMe: boolean
}>({
  me: null,
  loadingMe: false,
})

/**
 * 读取当前会话信息（内存态）
 */
export function useAuthState() {
  return state
}

export function hasRole(role: string): boolean {
  return Boolean(state.me?.roles?.includes(role))
}

export const isAdmin = computed(() => hasRole('ADMIN'))

export async function refreshMe(options?: { force?: boolean }) {
  if (state.loadingMe) {
    return state.me
  }
  if (state.me && !options?.force) {
    return state.me
  }

  state.loadingMe = true
  try {
    state.me = await getMe()
    return state.me
  } catch (e) {
    if (isAuthInvalidError(e)) {
      clearSession()
    }
    throw e
  } finally {
    state.loadingMe = false
  }
}

export function clearSession() {
  clearToken()
  state.me = null
}
