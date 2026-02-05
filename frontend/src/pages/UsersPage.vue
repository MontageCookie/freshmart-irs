<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { listUsers, type UserListItemResponse } from '@/api/usersApi'
import { ApiError } from '@/api/types'
import { hasRole, refreshMe, useAuthState } from '@/auth/authStore'

const router = useRouter()
const authState = useAuthState()

const isAdmin = computed(() => hasRole('ADMIN'))

const loading = ref(false)
const errorText = ref<string | null>(null)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const items = ref<UserListItemResponse[]>([])

const keyword = ref('')
const status = ref<'ALL' | 'ENABLED' | 'DISABLED'>('ALL')

function formatError(err: unknown): string {
  if (err instanceof ApiError) {
    return err.traceId ? `${err.message}\ntraceId: ${err.traceId}` : err.message
  }
  return '加载失败'
}

async function load() {
  loading.value = true
  errorText.value = null
  try {
    if (!authState.me) {
      await refreshMe()
    }
    if (!isAdmin.value) {
      return
    }
    const resp = await listUsers({
      page: page.value,
      size: size.value,
      keyword: keyword.value.trim() ? keyword.value.trim() : undefined,
      status: status.value === 'ALL' ? undefined : status.value,
    })
    items.value = resp.items
    total.value = resp.total
  } catch (e) {
    errorText.value = formatError(e)
  } finally {
    loading.value = false
  }
}

async function back() {
  await router.push('/me')
}

async function goCreate() {
  await router.push('/users/new')
}

async function openDetail(id: number) {
  await router.push(`/users/${id}`)
}

async function applyFilters() {
  page.value = 1
  await load()
}

function canPrev() {
  return page.value > 1
}

function canNext() {
  return page.value * size.value < total.value
}

async function prev() {
  if (!canPrev()) return
  page.value -= 1
  await load()
}

async function next() {
  if (!canNext()) return
  page.value += 1
  await load()
}

onMounted(load)
</script>

<template>
  <div class="page">
    <h2 class="title">用户列表</h2>

    <div class="actions">
      <button class="btn btn--secondary" type="button" @click="back">返回</button>
      <button class="btn btn--secondary" type="button" :disabled="!isAdmin" @click="goCreate">
        创建用户
      </button>
      <button class="btn" type="button" :disabled="loading" @click="load">
        {{ loading ? '刷新中...' : '刷新' }}
      </button>
    </div>

    <div v-if="!isAdmin" class="forbidden" role="alert">
      <div class="forbidden__title">无权限</div>
      <div class="forbidden__desc">当前账号不具备 ADMIN 角色，无法查看用户列表。</div>
    </div>

    <div v-else>
      <div v-if="errorText" class="error" role="alert">{{ errorText }}</div>

      <div v-else class="card">
        <div class="filters">
          <label class="filter">
            <span class="filter__label">keyword</span>
            <input v-model="keyword" class="input" autocomplete="off" @keydown.enter.prevent="applyFilters" />
          </label>
          <label class="filter">
            <span class="filter__label">status</span>
            <select v-model="status" class="select">
              <option value="ALL">ALL</option>
              <option value="ENABLED">ENABLED</option>
              <option value="DISABLED">DISABLED</option>
            </select>
          </label>
          <button class="btn" type="button" :disabled="loading" @click="applyFilters">筛选</button>
        </div>

        <div class="meta">page={{ page }}, size={{ size }}, total={{ total }}</div>

        <div class="table">
          <div class="tr th">
            <div>id</div>
            <div>username</div>
            <div>phone</div>
            <div>email</div>
            <div>status</div>
            <div>createdAt</div>
            <div></div>
          </div>

          <div v-for="u in items" :key="u.id" class="tr">
            <div>{{ u.id }}</div>
            <div>{{ u.username }}</div>
            <div>{{ u.phone ?? '-' }}</div>
            <div>{{ u.email ?? '-' }}</div>
            <div>{{ u.status }}</div>
            <div class="mono">{{ u.createdAt }}</div>
            <div class="ops">
              <button class="btn btn--link" type="button" @click="openDetail(u.id)">详情</button>
            </div>
          </div>

          <div v-if="!items.length && !loading" class="empty">暂无数据</div>
        </div>

        <div class="pager">
          <button class="btn" type="button" :disabled="!canPrev() || loading" @click="prev">上一页</button>
          <button class="btn" type="button" :disabled="!canNext() || loading" @click="next">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 18px;
  max-width: 1100px;
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

.btn--secondary {
  border-color: rgba(37, 99, 235, 0.3);
  color: rgba(37, 99, 235, 0.95);
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.65;
}

.forbidden {
  border: 1px solid rgba(245, 158, 11, 0.4);
  background: rgba(245, 158, 11, 0.06);
  border-radius: 12px;
  padding: 14px;
}

.forbidden__title {
  font-weight: 700;
  margin-bottom: 6px;
}

.forbidden__desc {
  color: rgba(0, 0, 0, 0.65);
  font-size: 14px;
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

.filters {
  display: grid;
  grid-template-columns: 1fr 200px auto;
  align-items: end;
  gap: 10px;
  margin-bottom: 10px;
}

.filter {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter__label {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.6);
}

.input,
.select {
  width: 100%;
  border: 1px solid rgba(0, 0, 0, 0.18);
  border-radius: 10px;
  padding: 9px 12px;
  font-size: 13px;
  background: #fff;
}

.input:focus,
.select:focus {
  outline: none;
  border-color: rgba(37, 99, 235, 0.6);
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
}

.meta {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.55);
  margin-bottom: 10px;
}

.table {
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 10px;
  overflow: hidden;
}

.tr {
  display: grid;
  grid-template-columns: 80px 160px 160px 220px 120px 1fr 90px;
  gap: 10px;
  padding: 10px 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  font-size: 14px;
}

.tr:last-child {
  border-bottom: none;
}

.th {
  background: rgba(0, 0, 0, 0.03);
  font-weight: 600;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.7);
}

.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
    monospace;
  font-size: 12px;
}

.empty {
  padding: 14px 12px;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.55);
}

.ops {
  display: flex;
  justify-content: flex-end;
}

.btn--link {
  border-color: rgba(37, 99, 235, 0.3);
  color: rgba(37, 99, 235, 0.95);
  background: #fff;
}

.pager {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

@media (max-width: 980px) {
  .filters {
    grid-template-columns: 1fr;
    align-items: stretch;
  }
  .tr {
    grid-template-columns: 70px 140px 140px 200px 110px 1fr 80px;
  }
}
</style>
