<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { refreshMe, useAuthState } from '@/auth/authStore'
import { ApiError } from '@/api/types'
import { updateUser } from '@/api/usersApi'

const authState = useAuthState()

const loading = ref(false)
const errorText = ref<string | null>(null)

const savingProfile = ref(false)
const savingPassword = ref(false)

const profileUsername = ref('')
const profilePhone = ref('')
const profileEmail = ref('')
const profileCurrentPassword = ref('')

const passwordCurrentPassword = ref('')
const passwordNewPassword = ref('')

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '加载失败'
}

function syncFormFromMe() {
  const me = authState.me
  if (!me) return
  profileUsername.value = me.username || ''
  profilePhone.value = me.phone || ''
  profileEmail.value = me.email || ''
}

async function load() {
  loading.value = true
  errorText.value = null
  try {
    await refreshMe({ force: true })
    syncFormFromMe()
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  errorText.value = null
  const me = authState.me
  if (!me) {
    errorText.value = '未登录'
    return
  }

  const username = profileUsername.value.trim()
  if (!username) {
    errorText.value = 'username 不能为空'
    return
  }
  if (!profileCurrentPassword.value) {
    errorText.value = 'currentPassword 不能为空'
    return
  }

  savingProfile.value = true
  try {
    await updateUser(me.id, {
      username,
      phone: profilePhone.value.trim() ? profilePhone.value.trim() : null,
      email: profileEmail.value.trim() ? profileEmail.value.trim() : null,
      currentPassword: profileCurrentPassword.value,
    })
    profileCurrentPassword.value = ''
    await load()
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    savingProfile.value = false
  }
}

async function changePassword() {
  errorText.value = null
  const me = authState.me
  if (!me) {
    errorText.value = '未登录'
    return
  }
  if (!passwordCurrentPassword.value) {
    errorText.value = 'currentPassword 不能为空'
    return
  }
  if (!passwordNewPassword.value) {
    errorText.value = 'newPassword 不能为空'
    return
  }

  savingPassword.value = true
  try {
    await updateUser(me.id, {
      currentPassword: passwordCurrentPassword.value,
      newPassword: passwordNewPassword.value,
    })
    passwordCurrentPassword.value = ''
    passwordNewPassword.value = ''
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    savingPassword.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <h2 class="title">我的信息（顾客端）</h2>

    <div class="actions">
      <button class="btn" type="button" :disabled="loading" @click="load">
        {{ loading ? '刷新中...' : '刷新' }}
      </button>
    </div>

    <div v-if="errorText" class="error" role="alert">{{ errorText }}</div>

    <div v-else class="grid">
      <div class="card">
        <div class="card__title">概览</div>
        <div class="row"><span class="k">id</span><span class="v">{{ authState.me?.id ?? '-' }}</span></div>
        <div class="row"><span class="k">username</span><span class="v">{{ authState.me?.username ?? '-' }}</span></div>
        <div class="row"><span class="k">phone</span><span class="v">{{ authState.me?.phone ?? '-' }}</span></div>
        <div class="row"><span class="k">email</span><span class="v">{{ authState.me?.email ?? '-' }}</span></div>
        <div class="row"><span class="k">status</span><span class="v">{{ authState.me?.status ?? '-' }}</span></div>
        <div class="row">
          <span class="k">roles</span><span class="v">{{ authState.me?.roles?.length ? authState.me.roles.join(', ') : '-' }}</span>
        </div>
      </div>

      <div class="card">
        <div class="card__title">更新资料（自助）</div>
        <div class="form">
          <label class="field">
            <span class="field__label">username *</span>
            <input v-model="profileUsername" class="input" autocomplete="off" />
          </label>
          <label class="field">
            <span class="field__label">phone</span>
            <input v-model="profilePhone" class="input" autocomplete="off" />
          </label>
          <label class="field">
            <span class="field__label">email</span>
            <input v-model="profileEmail" class="input" autocomplete="off" />
          </label>
          <label class="field">
            <span class="field__label">currentPassword *</span>
            <input v-model="profileCurrentPassword" class="input" type="password" autocomplete="current-password" />
          </label>
        </div>
        <div class="actions">
          <button class="btn btn--primary" type="button" :disabled="savingProfile" @click="saveProfile">
            {{ savingProfile ? '保存中...' : '保存资料' }}
          </button>
        </div>
      </div>

      <div class="card">
        <div class="card__title">修改密码（自助）</div>
        <div class="form">
          <label class="field">
            <span class="field__label">currentPassword *</span>
            <input v-model="passwordCurrentPassword" class="input" type="password" autocomplete="current-password" />
          </label>
          <label class="field">
            <span class="field__label">newPassword *</span>
            <input v-model="passwordNewPassword" class="input" type="password" autocomplete="new-password" />
          </label>
        </div>
        <div class="actions">
          <button class="btn btn--primary" type="button" :disabled="savingPassword" @click="changePassword">
            {{ savingPassword ? '提交中...' : '修改密码' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 18px;
  max-width: 920px;
  margin: 0 auto;
}

.title {
  margin: 0 0 12px;
  font-size: 18px;
}

.actions {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.btn {
  border: 1px solid rgba(0, 0, 0, 0.14);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.65;
}

.error {
  border: 1px solid rgba(214, 48, 49, 0.35);
  background: rgba(214, 48, 49, 0.06);
  color: rgba(214, 48, 49, 0.95);
  border-radius: 10px;
  padding: 10px 12px;
  white-space: pre-wrap;
}

.card {
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 12px;
  padding: 14px;
  background: #fff;
}

.grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.card__title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 10px;
}

.row {
  display: grid;
  grid-template-columns: 140px 1fr;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px dashed rgba(0, 0, 0, 0.08);
}

.row:last-child {
  border-bottom: none;
}

.k {
  color: rgba(0, 0, 0, 0.55);
  font-size: 13px;
}

.v {
  font-size: 14px;
  word-break: break-word;
}

.form {
  display: grid;
  gap: 10px;
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
}

.input:focus {
  outline: none;
  border-color: rgba(37, 99, 235, 0.6);
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
}

.actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.btn--primary {
  border-color: rgba(37, 99, 235, 0.3);
  background: #2563eb;
  color: #fff;
}
</style>
