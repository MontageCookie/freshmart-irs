<script setup lang="ts">
import { computed, ref } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'

import { logout } from '@/modules/security/api/authApi'
import { clearSession, useAuthState } from '@/auth/authStore'
import { getToken } from '@/auth/token'
import { canAccessCustomerPortal } from '@/auth/roleGuards'

const router = useRouter()
const authState = useAuthState()

const hasToken = computed(() => Boolean(getToken()))
const displayName = computed(() => authState.me?.username || '未登录')
const rolesText = computed(() => (authState.me?.roles?.length ? authState.me.roles.join(', ') : '-'))
const canSeeCustomerMe = computed(() => canAccessCustomerPortal(authState.me?.roles))

const loggingOut = ref(false)

async function onLogout() {
  if (!hasToken.value || loggingOut.value) return
  loggingOut.value = true
  try {
    await logout()
  } catch {
  } finally {
    clearSession()
    await router.replace({ name: 'cLogin' })
    loggingOut.value = false
  }
}
</script>

<template>
  <div class="layout">
    <header class="topbar">
      <div class="brand">Freshmart IRS 顾客端</div>
      <nav class="nav">
        <RouterLink class="nav__link" to="/products">/products</RouterLink>
        <RouterLink v-if="hasToken && canSeeCustomerMe" class="nav__link" to="/me">/me</RouterLink>
        <RouterLink v-if="!hasToken" class="nav__link" to="/customer/login">/customer/login</RouterLink>
        <RouterLink v-if="!hasToken" class="nav__link" to="/customer/register">/customer/register</RouterLink>
      </nav>
      <div class="session">
        <div class="session__meta">
          <div class="session__user">{{ displayName }}</div>
          <div class="session__roles">{{ rolesText }}</div>
        </div>
        <button class="btn" type="button" :disabled="!hasToken || loggingOut" @click="onLogout">
          {{ loggingOut ? '登出中...' : '登出' }}
        </button>
      </div>
    </header>

    <main class="main">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  background: #f6f7fb;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  background: #fff;
}

.brand {
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 0.2px;
}

.nav {
  display: flex;
  gap: 10px;
  align-items: center;
}

.nav__link {
  display: inline-flex;
  align-items: center;
  border: 1px solid rgba(0, 0, 0, 0.14);
  border-radius: 10px;
  padding: 6px 10px;
  text-decoration: none;
  color: rgba(0, 0, 0, 0.78);
  font-size: 13px;
}

.nav__link.router-link-active {
  border-color: rgba(37, 99, 235, 0.3);
  color: rgba(37, 99, 235, 0.95);
  background: rgba(37, 99, 235, 0.06);
}

.session {
  display: flex;
  align-items: center;
  gap: 10px;
}

.session__meta {
  display: grid;
  gap: 2px;
  text-align: right;
}

.session__user {
  font-size: 13px;
  color: rgba(0, 0, 0, 0.85);
}

.session__roles {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.55);
}

.btn {
  border: 1px solid rgba(0, 0, 0, 0.14);
  background: #fff;
  border-radius: 10px;
  padding: 6px 10px;
  cursor: pointer;
  font-size: 13px;
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.main {
  padding: 0;
}
</style>
