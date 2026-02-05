import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

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
  const redirect = router.currentRoute.value.fullPath
  if (router.currentRoute.value.name !== 'login') {
    router.replace({ name: 'login', query: { redirect } })
  }
})

createApp(App).use(router).mount('#app')
