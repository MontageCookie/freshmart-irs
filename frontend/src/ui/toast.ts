import { reactive } from 'vue'

export type ToastType = 'info' | 'error'

export interface ToastItem {
  id: number
  type: ToastType
  message: string
}

const state = reactive<{ items: ToastItem[] }>({
  items: [],
})

let nextId = 1

export function useToastState() {
  return state
}

export function pushToast(message: string, type: ToastType = 'error', ttlMs = 6_000) {
  const id = nextId++
  state.items.unshift({ id, type, message })
  window.setTimeout(() => removeToast(id), ttlMs)
}

export function removeToast(id: number) {
  const idx = state.items.findIndex((x) => x.id === id)
  if (idx >= 0) {
    state.items.splice(idx, 1)
  }
}
