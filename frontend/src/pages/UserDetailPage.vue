<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { listRoles, type RoleResponse } from '@/api/rolesApi'
import {
  disableUser,
  getUser,
  updateUser,
  updateUserRoles,
  type UserDetailResponse,
} from '@/api/usersApi'
import { ApiError } from '@/api/types'

const router = useRouter()
const route = useRoute()

const userId = computed(() => Number(route.params.id))

const loading = ref(false)
const errorText = ref<string | null>(null)

const user = ref<UserDetailResponse | null>(null)
const roles = ref<RoleResponse[]>([])

const roleCodeToId = computed(() => {
  const map = new Map<string, number>()
  for (const r of roles.value) {
    map.set(r.code, r.id)
  }
  return map
})

const selectedRoleIds = ref<number[]>([])

const rolesForDisplay = computed(() => {
  const list = [...roles.value]
  list.sort((a, b) => {
    if (a.enabled !== b.enabled) return a.enabled ? -1 : 1
    return a.code.localeCompare(b.code)
  })
  return list
})

const formUsername = ref('')
const formPhone = ref('')
const formEmail = ref('')

const newPassword = ref('')

const savingInfo = ref(false)
const savingPassword = ref(false)
const savingRoles = ref(false)
const disabling = ref(false)

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '操作失败'
}

function syncFormFromUser(u: UserDetailResponse) {
  formUsername.value = u.username || ''
  formPhone.value = u.phone || ''
  formEmail.value = u.email || ''
  const ids: number[] = []
  for (const code of u.roles || []) {
    const id = roleCodeToId.value.get(code)
    if (typeof id === 'number') {
      ids.push(id)
    }
  }
  selectedRoleIds.value = Array.from(new Set(ids))
}

async function load() {
  if (!Number.isFinite(userId.value) || userId.value <= 0) {
    user.value = null
    roles.value = []
    errorText.value = '非法用户 ID'
    return
  }
  loading.value = true
  errorText.value = null
  try {
    const [roleResp, userResp] = await Promise.all([listRoles(), getUser(userId.value)])
    roles.value = roleResp
    user.value = userResp
    syncFormFromUser(userResp)
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    loading.value = false
  }
}

async function back() {
  await router.push('/users')
}

function toggleRole(id: number) {
  const idx = selectedRoleIds.value.indexOf(id)
  if (idx >= 0) {
    selectedRoleIds.value.splice(idx, 1)
  } else {
    selectedRoleIds.value.push(id)
  }
}

async function saveInfo() {
  if (!user.value) return
  errorText.value = null

  const username = formUsername.value.trim()
  if (!username) {
    errorText.value = 'username 不能为空'
    return
  }

  savingInfo.value = true
  try {
    await updateUser(user.value.id, {
      username,
      phone: formPhone.value.trim() ? formPhone.value.trim() : null,
      email: formEmail.value.trim() ? formEmail.value.trim() : null,
    })
    await load()
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    savingInfo.value = false
  }
}

async function resetPassword() {
  if (!user.value) return
  errorText.value = null

  if (!newPassword.value) {
    errorText.value = 'newPassword 不能为空'
    return
  }

  savingPassword.value = true
  try {
    await updateUser(user.value.id, { newPassword: newPassword.value })
    newPassword.value = ''
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    savingPassword.value = false
  }
}

async function saveRoles() {
  if (!user.value) return
  errorText.value = null

  if (selectedRoleIds.value.length === 0) {
    errorText.value = 'roleIds 不能为空'
    return
  }

  savingRoles.value = true
  try {
    await updateUserRoles(user.value.id, { roleIds: [...selectedRoleIds.value] })
    await load()
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    savingRoles.value = false
  }
}

async function onDisable() {
  if (!user.value) return
  if (user.value.status === 'DISABLED') return

  const ok = window.confirm(`确认禁用用户：${user.value.username}（id=${user.value.id}）？`)
  if (!ok) return

  disabling.value = true
  errorText.value = null
  try {
    await disableUser(user.value.id)
    await load()
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    disabling.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="head">
      <div>
        <h2 class="title">用户详情</h2>
        <div class="sub" v-if="user">id={{ user.id }}, status={{ user.status }}</div>
      </div>
      <div class="head__actions">
        <button class="btn btn--secondary" type="button" @click="back">返回列表</button>
        <button class="btn" type="button" :disabled="loading" @click="load">
          {{ loading ? '刷新中...' : '刷新' }}
        </button>
      </div>
    </div>

    <div v-if="errorText" class="error" role="alert">{{ errorText }}</div>

    <div v-if="!user && !loading" class="card">未找到用户</div>

    <div v-else class="grid">
      <div class="card">
        <div class="card__title">基本信息</div>
        <div class="form">
          <label class="field">
            <span class="field__label">username *</span>
            <input v-model="formUsername" class="input" autocomplete="off" />
          </label>
          <label class="field">
            <span class="field__label">phone</span>
            <input v-model="formPhone" class="input" autocomplete="off" />
          </label>
          <label class="field">
            <span class="field__label">email</span>
            <input v-model="formEmail" class="input" autocomplete="off" />
          </label>
        </div>
        <div class="actions">
          <button class="btn btn--primary" type="button" :disabled="savingInfo" @click="saveInfo">
            {{ savingInfo ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>

      <div class="card">
        <div class="card__title">重置密码（管理员）</div>
        <div class="form">
          <label class="field">
            <span class="field__label">newPassword *</span>
            <input v-model="newPassword" class="input" type="password" autocomplete="new-password" />
          </label>
        </div>
        <div class="actions">
          <button class="btn btn--primary" type="button" :disabled="savingPassword" @click="resetPassword">
            {{ savingPassword ? '提交中...' : '重置密码' }}
          </button>
        </div>
      </div>

      <div class="card">
        <div class="card__title">分配角色（覆盖式）</div>
        <div class="roles">
          <label v-for="r in rolesForDisplay" :key="r.id" class="role" :data-disabled="!r.enabled">
            <input
              class="role__cb"
              type="checkbox"
              :checked="selectedRoleIds.includes(r.id)"
              :disabled="!r.enabled"
              @change="toggleRole(r.id)"
            />
            <span class="role__name">{{ r.name }}</span>
            <span class="role__code">{{ r.code }}</span>
          </label>
          <div v-if="!rolesForDisplay.length" class="empty">暂无角色</div>
        </div>
        <div class="actions">
          <button class="btn btn--primary" type="button" :disabled="savingRoles" @click="saveRoles">
            {{ savingRoles ? '保存中...' : '保存角色' }}
          </button>
        </div>
        <div class="tip" v-if="user">当前角色：{{ user.roles?.length ? user.roles.join(', ') : '-' }}</div>
      </div>

      <div class="card">
        <div class="card__title">禁用用户</div>
        <div class="tip">
          语义固定：调用 <span class="mono">DELETE /api/v1/users/{id}</span> 将用户状态置为 DISABLED
        </div>
        <div class="actions">
          <button
            class="btn btn--danger"
            type="button"
            :disabled="disabling || user?.status === 'DISABLED'"
            @click="onDisable"
          >
            {{ user?.status === 'DISABLED' ? '已禁用' : disabling ? '处理中...' : '禁用' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 18px;
  max-width: 1120px;
  margin: 0 auto;
}

.head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.title {
  margin: 0;
  font-size: 18px;
}

.sub {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.55);
}

.head__actions {
  display: flex;
  gap: 10px;
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

.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.card {
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 12px;
  padding: 14px;
  background: #fff;
}

.card__title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 10px;
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

.role[data-disabled='true'] {
  opacity: 0.7;
}

.role[data-disabled='true'] .role__code {
  color: rgba(0, 0, 0, 0.4);
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
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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

.btn--danger {
  border-color: rgba(214, 48, 49, 0.35);
  background: rgba(214, 48, 49, 0.95);
  color: #fff;
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.65;
}

.tip {
  margin-top: 10px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.55);
}

.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
    monospace;
  font-size: 12px;
}

@media (max-width: 920px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .roles {
    grid-template-columns: 1fr;
  }
}
</style>
