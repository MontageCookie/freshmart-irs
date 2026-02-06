<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'

import { login } from '@/modules/security/api/authApi'
import { clearSession, refreshMe } from '@/auth/authStore'
import { setToken } from '@/auth/token'
import { ApiError } from '@/api/types'
import { canAccessCustomerPortal } from '@/auth/roleGuards'

const router = useRouter()
const route = useRoute()

const username = ref('')
const password = ref('')
const submitting = ref(false)
const errorText = ref<string | null>(null)

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '登录失败'
}

async function onSubmit() {
  errorText.value = null
  if (!username.value.trim() || !password.value) {
    errorText.value = '请输入用户名与密码'
    return
  }

  submitting.value = true
  try {
    const resp = await login(username.value.trim(), password.value)
    setToken(resp.token)
    const me = await refreshMe({ force: true })
    if (!canAccessCustomerPortal(me?.roles)) {
      clearSession()
      errorText.value = '当前账号无权限登录顾客端，请使用管理端入口 /manager/login'
      return
    }
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/products'
    await router.replace(redirect)
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="card">
      <h1 class="title">Freshmart IRS 顾客端</h1>
      <p class="subtitle">账号与角色模块（A2）</p>

      <form class="form" @submit.prevent="onSubmit">
        <label class="field">
          <span class="field__label">用户名</span>
          <input v-model="username" class="input" type="text" autocomplete="username" />
        </label>

        <label class="field">
          <span class="field__label">密码</span>
          <input v-model="password" class="input" type="password" autocomplete="current-password" />
        </label>

        <div v-if="errorText" class="error" role="alert">{{ errorText }}</div>

        <button class="btn" type="submit" :disabled="submitting">
          {{ submitting ? '登录中...' : '登录' }}
        </button>
      </form>

      <div class="hint">
        <div>没有账号？<RouterLink to="/customer/register">去注册</RouterLink></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}

.card {
  width: min(460px, 100%);
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 12px;
  background: #fff;
  padding: 22px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
}

.title {
  margin: 0;
  font-size: 20px;
}

.subtitle {
  margin: 6px 0 18px;
  color: rgba(0, 0, 0, 0.55);
  font-size: 13px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field__label {
  font-size: 13px;
  color: rgba(0, 0, 0, 0.7);
}

.input {
  width: 100%;
  border: 1px solid rgba(0, 0, 0, 0.18);
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
}

.input:focus {
  border-color: rgba(37, 99, 235, 0.45);
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.12);
}

.btn {
  border: 1px solid rgba(0, 0, 0, 0.14);
  background: rgba(37, 99, 235, 0.95);
  color: #fff;
  border-radius: 10px;
  padding: 10px 12px;
  cursor: pointer;
  font-size: 14px;
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.error {
  white-space: pre-wrap;
  border: 1px solid rgba(239, 68, 68, 0.25);
  background: rgba(239, 68, 68, 0.06);
  padding: 10px 12px;
  border-radius: 10px;
  color: rgba(239, 68, 68, 0.95);
  font-size: 13px;
}

.hint {
  margin-top: 14px;
  color: rgba(0, 0, 0, 0.6);
  font-size: 13px;
  display: grid;
  gap: 6px;
}

.hint a {
  color: rgba(37, 99, 235, 0.95);
  text-decoration: none;
}

.hint a:hover {
  text-decoration: underline;
}
</style>
