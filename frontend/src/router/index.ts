import { createRouter, createWebHistory } from 'vue-router'

import { refreshMe, useAuthState } from '@/auth/authStore'
import { getToken } from '@/auth/token'
import { isAuthInvalidError } from '@/api/types'
import { pushToast } from '@/ui/toast'
import { canAccessAdminConsole, canAccessCustomerPortal } from '@/auth/roleGuards'

const LoginPage = () => import('@/pages/LoginPage.vue')
const MePage = () => import('@/pages/MePage.vue')
const UsersPage = () => import('@/pages/UsersPage.vue')
const UserCreatePage = () => import('@/pages/UserCreatePage.vue')
const UserDetailPage = () => import('@/pages/UserDetailPage.vue')
const CustomerLoginPage = () => import('@/pages/CustomerLoginPage.vue')
const CustomerRegisterPage = () => import('@/pages/CustomerRegisterPage.vue')
const CustomerMePage = () => import('@/pages/CustomerMePage.vue')

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/me' },
    { path: '/c', redirect: '/c/me' },
    { path: '/login', name: 'login', component: LoginPage },
    { path: '/c/login', name: 'cLogin', component: CustomerLoginPage },
    { path: '/c/register', name: 'cRegister', component: CustomerRegisterPage },
    { path: '/me', name: 'me', component: MePage, meta: { requiresAuth: true } },
    { path: '/c/me', name: 'cMe', component: CustomerMePage, meta: { requiresAuth: true } },
    { path: '/users', name: 'users', component: UsersPage, meta: { requiresAuth: true, roles: ['ADMIN'] } },
    {
      path: '/users/new',
      name: 'userCreate',
      component: UserCreatePage,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/users/:id',
      name: 'userDetail',
      component: UserDetailPage,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
  ],
})

router.beforeEach(async (to) => {
  const requiresAuth = Boolean(to.meta.requiresAuth)
  const token = getToken()
  const isCustomerRoute = to.path === '/c' || to.path.startsWith('/c/')
  const loginRouteName = isCustomerRoute ? 'cLogin' : 'login'
  const homeRouteName = isCustomerRoute ? 'cMe' : 'me'

  if (requiresAuth && !token) {
    return { name: loginRouteName, query: { redirect: to.fullPath } }
  }

  if ((to.name === 'login' || to.name === 'cLogin' || to.name === 'cRegister') && token) {
    return { name: homeRouteName }
  }

  if (token && requiresAuth) {
    const authState = useAuthState()
    if (!authState.me && !authState.loadingMe) {
      try {
        await refreshMe()
      } catch (e) {
        if (isAuthInvalidError(e)) {
          return { name: loginRouteName, query: { redirect: to.fullPath } }
        }
      }
    }

    if (isCustomerRoute) {
      if (!canAccessCustomerPortal(authState.me?.roles)) {
        pushToast('无权限访问顾客端，请使用管理端入口', 'error')
        return { name: 'me' }
      }
    } else {
      if (!canAccessAdminConsole(authState.me?.roles)) {
        pushToast('无权限访问管理端，请使用顾客端入口', 'error')
        return { name: 'cMe' }
      }
    }

    const requiredRoles = Array.isArray(to.meta.roles) ? (to.meta.roles as string[]) : []
    if (requiredRoles.length > 0) {
      const roles = authState.me?.roles || []
      const ok = requiredRoles.every((r) => roles.includes(r))
      if (!ok) {
        pushToast(`无权限访问：${to.fullPath}`, 'error')
        return { name: homeRouteName }
      }
    }
  }

  return true
})
