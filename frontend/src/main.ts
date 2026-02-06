import { createApp } from 'vue'
import './style.css'
import 'element-plus/dist/index.css'
import App from './App.vue'
import ElementPlus from 'element-plus'

import { setAuthInvalidHandler, setErrorNotifier } from '@/api/apiClient'
import { ApiError, isAuthInvalidError } from '@/api/types'
import { clearSession } from '@/auth/authStore'
import { router } from '@/router'
import { pushToast } from '@/ui/toast'

setErrorNotifier((err: ApiError) => {
  const text = err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  pushToast(text, 'error')
})

setAuthInvalidHandler((err: ApiError) => {
  if (!isAuthInvalidError(err)) {
    return
  }
  clearSession()
  const currentRoute = router.currentRoute.value
  const isManagerArea = currentRoute.path.startsWith('/manager')
  const redirect = currentRoute.fullPath
  const loginRouteName = isManagerArea ? 'login' : 'cLogin'
  if (currentRoute.name !== loginRouteName) {
    router.replace({ name: loginRouteName, query: { redirect } })
  }
})

createApp(App).use(router).use(ElementPlus).mount('#app')
