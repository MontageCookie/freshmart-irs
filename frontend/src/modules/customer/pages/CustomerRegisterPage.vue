<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'

import { register } from '@/modules/security/api/authApi'
import { ApiError } from '@/api/types'
import { pushToast } from '@/ui/toast'

const router = useRouter()

const username = ref('')
const password = ref('')
const phone = ref('')
const email = ref('')

const submitting = ref(false)
const errorText = ref<string | null>(null)

const phoneTrimmed = computed(() => (phone.value.trim() ? phone.value.trim() : ''))
const emailTrimmed = computed(() => (email.value.trim() ? email.value.trim() : ''))

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '注册失败'
}

function validate(): string | null {
  if (!username.value.trim()) return '请输入用户名'
  if (!password.value) return '请输入密码'
  if (phoneTrimmed.value) {
    const digits = phoneTrimmed.value.replace(/\D/g, '')
    if (digits.length !== 11) return 'phone 需为 11 位手机号'
  }
  if (emailTrimmed.value && !emailTrimmed.value.includes('@')) return 'email 格式不正确'
  return null
}

async function onSubmit() {
  errorText.value = null
  const msg = validate()
  if (msg) {
    errorText.value = msg
    return
  }

  submitting.value = true
  try {
    await register({
      username: username.value.trim(),
      password: password.value,
      phone: phoneTrimmed.value ? phoneTrimmed.value : null,
      email: emailTrimmed.value ? emailTrimmed.value : null,
    })
    pushToast('注册成功，请登录', 'info')
    await router.replace('/customer/login')
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
      <h1 class="title">顾客注册</h1>
      <p class="subtitle">注册成功后请使用新账号登录</p>

      <form class="form" @submit.prevent="onSubmit">
        <label class="field">
          <span class="field__label">username *</span>
          <input v-model="username" class="input" autocomplete="off" />
        </label>

        <label class="field">
          <span class="field__label">password *</span>
          <input v-model="password" class="input" type="password" autocomplete="new-password" />
        </label>

        <label class="field">
          <span class="field__label">phone</span>
          <input v-model="phone" class="input" autocomplete="off" />
        </label>

        <label class="field">
          <span class="field__label">email</span>
          <input v-model="email" class="input" autocomplete="off" />
        </label>

        <div v-if="errorText" class="error" role="alert">{{ errorText }}</div>

        <button class="btn" type="submit" :disabled="submitting">
          {{ submitting ? '提交中...' : '注册' }}
        </button>
      </form>

      <div class="hint">
        <div>已有账号？<RouterLink to="/customer/login">去登录</RouterLink></div>
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
