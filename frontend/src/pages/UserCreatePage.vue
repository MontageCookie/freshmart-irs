<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { listRoles, type RoleResponse } from '@/api/rolesApi'
import { createUser } from '@/api/usersApi'
import { ApiError } from '@/api/types'

const router = useRouter()

const loading = ref(false)
const errorText = ref<string | null>(null)

const username = ref('')
const password = ref('')
const phone = ref('')
const email = ref('')

const roles = ref<RoleResponse[]>([])
const selectedRoleIds = ref<number[]>([])

const enabledRoles = computed(() => roles.value.filter((r) => r.enabled))

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '操作失败'
}

async function loadRoles() {
  const resp = await listRoles()
  roles.value = resp
}

function toggleRole(id: number) {
  const idx = selectedRoleIds.value.indexOf(id)
  if (idx >= 0) {
    selectedRoleIds.value.splice(idx, 1)
  } else {
    selectedRoleIds.value.push(id)
  }
}

function validate(): string | null {
  if (!username.value.trim()) return '请输入用户名'
  if (!password.value) return '请输入初始密码'
  if (selectedRoleIds.value.length === 0) return '请选择至少一个角色'
  return null
}

async function onSubmit() {
  errorText.value = null
  const msg = validate()
  if (msg) {
    errorText.value = msg
    return
  }

  loading.value = true
  try {
    const resp = await createUser({
      username: username.value.trim(),
      password: password.value,
      phone: phone.value.trim() ? phone.value.trim() : null,
      email: email.value.trim() ? email.value.trim() : null,
      roleIds: [...selectedRoleIds.value],
    })
    await router.replace(`/users/${resp.id}`)
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    loading.value = false
  }
}

async function back() {
  await router.push('/users')
}

onMounted(async () => {
  try {
    await loadRoles()
  } catch (e) {
    errorText.value = formatError(e)
  }
})
</script>

<template>
  <div class="page">
    <div class="head">
      <h2 class="title">创建用户</h2>
      <button class="btn btn--secondary" type="button" @click="back">返回列表</button>
    </div>

    <div v-if="errorText" class="error" role="alert">{{ errorText }}</div>

    <form class="card" @submit.prevent="onSubmit">
      <div class="grid">
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
      </div>

      <div class="section">
        <div class="section__title">角色选择 *</div>
        <div class="roles">
          <label v-for="r in enabledRoles" :key="r.id" class="role">
            <input
              class="role__cb"
              type="checkbox"
              :checked="selectedRoleIds.includes(r.id)"
              @change="toggleRole(r.id)"
            />
            <span class="role__name">{{ r.name }}</span>
            <span class="role__code">{{ r.code }}</span>
          </label>
          <div v-if="!enabledRoles.length" class="empty">暂无可用角色</div>
        </div>
      </div>

      <div class="actions">
        <button class="btn btn--primary" type="submit" :disabled="loading">
          {{ loading ? '提交中...' : '创建' }}
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.page {
  padding: 18px;
  max-width: 980px;
  margin: 0 auto;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.title {
  margin: 0;
  font-size: 18px;
}

.error {
  border: 1px solid rgba(214, 48, 49, 0.35);
  background: rgba(214, 48, 49, 0.06);
  color: rgba(214, 48, 49, 0.95);
  border-radius: 10px;
  padding: 10px 12px;
  white-space: pre-wrap;
  margin-bottom: 12px;
}

.card {
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 12px;
  padding: 14px;
  background: #fff;
}

.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
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
}

.input:focus {
  outline: none;
  border-color: rgba(37, 99, 235, 0.6);
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
}

.section {
  margin-top: 14px;
}

.section__title {
  font-size: 13px;
  color: rgba(0, 0, 0, 0.7);
  margin-bottom: 8px;
}

.roles {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.role {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  background: #fff;
}

.role__cb {
  width: 16px;
  height: 16px;
}

.role__name {
  font-size: 14px;
}

.role__code {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.55);
}

.empty {
  font-size: 13px;
  color: rgba(0, 0, 0, 0.55);
}

.actions {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}

.btn {
  border: 1px solid rgba(0, 0, 0, 0.14);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
  font-size: 13px;
}

.btn--secondary {
  border-color: rgba(37, 99, 235, 0.3);
  color: rgba(37, 99, 235, 0.95);
}

.btn--primary {
  border-color: rgba(37, 99, 235, 0.3);
  background: #2563eb;
  color: #fff;
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.65;
}

@media (max-width: 820px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .roles {
    grid-template-columns: 1fr;
  }
}
</style>

