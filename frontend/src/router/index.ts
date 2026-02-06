import { createRouter, createWebHistory } from 'vue-router'

import { clearSession, refreshMe, useAuthState } from '@/auth/authStore'
import { getToken } from '@/auth/token'
import { isAuthInvalidError } from '@/api/types'
import { pushToast } from '@/ui/toast'
import { canAccessAdminConsole, canAccessCustomerPortal, isCashier } from '@/auth/roleGuards'

const LoginPage = () => import('@/pages/LoginPage.vue')
const MePage = () => import('@/pages/MePage.vue')
const UsersPage = () => import('@/pages/UsersPage.vue')
const UserCreatePage = () => import('@/pages/UserCreatePage.vue')
const UserDetailPage = () => import('@/pages/UserDetailPage.vue')
const CustomerLoginPage = () => import('@/pages/CustomerLoginPage.vue')
const CustomerRegisterPage = () => import('@/pages/CustomerRegisterPage.vue')
const CustomerMePage = () => import('@/pages/CustomerMePage.vue')
const CustomerProductsPage = () => import('@/pages/CustomerProductsPage.vue')
const CustomerProductDetailPage = () => import('@/pages/CustomerProductDetailPage.vue')
const ProductsPage = () => import('@/pages/ProductsPage.vue')
const ProductCreatePage = () => import('@/pages/ProductCreatePage.vue')
const ProductDetailPage = () => import('@/pages/ProductDetailPage.vue')
const InventorySnapshotPage = () => import('@/pages/InventorySnapshotPage.vue')
const InventoryLedgersPage = () => import('@/pages/InventoryLedgersPage.vue')

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/me' },
    { path: '/c', redirect: '/c/products' },
    { path: '/login', name: 'login', component: LoginPage },
    { path: '/c/login', name: 'cLogin', component: CustomerLoginPage },
    { path: '/c/register', name: 'cRegister', component: CustomerRegisterPage },
    { path: '/c/products', name: 'cProducts', component: CustomerProductsPage },
    { path: '/c/products/:id', name: 'cProductDetail', component: CustomerProductDetailPage },
    { path: '/me', name: 'me', component: MePage, meta: { requiresAuth: true } },
    { path: '/c/me', name: 'cMe', component: CustomerMePage, meta: { requiresAuth: true } },
    {
      path: '/products',
      name: 'products',
      component: ProductsPage,
      meta: { requiresAuth: true, roles: ['STORE_MANAGER', 'WAREHOUSE'] },
    },
    {
      path: '/products/new',
      name: 'productCreate',
      component: ProductCreatePage,
      meta: { requiresAuth: true, roles: ['STORE_MANAGER'] },
    },
    {
      path: '/products/:id',
      name: 'productDetail',
      component: ProductDetailPage,
      meta: { requiresAuth: true, roles: ['STORE_MANAGER', 'WAREHOUSE'] },
    },
    {
      path: '/inventory',
      name: 'inventorySnapshot',
      component: InventorySnapshotPage,
      meta: { requiresAuth: true, roles: ['STORE_MANAGER', 'WAREHOUSE'] },
    },
    {
      path: '/inventory/ledgers',
      name: 'inventoryLedgers',
      component: InventoryLedgersPage,
      meta: { requiresAuth: true, roles: ['STORE_MANAGER', 'WAREHOUSE'] },
    },
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

  if (token && !isCustomerRoute) {
    const authState = useAuthState()
    if (!authState.me && !authState.loadingMe) {
      try {
        await refreshMe()
      } catch (e) {
        if (isAuthInvalidError(e)) {
          return { name: 'login', query: { redirect: to.fullPath } }
        }
      }
    }
    if (isCashier(authState.me?.roles)) {
      clearSession()
      pushToast('无权进入管理后台', 'error')
      return { path: '/c/products' }
    }
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
        return { path: '/c/products' }
      }
    }

    const requiredRoles = Array.isArray(to.meta.roles) ? (to.meta.roles as string[]) : []
    if (requiredRoles.length > 0) {
      const roles = authState.me?.roles || []
      const ok = requiredRoles.some((r) => roles.includes(r))
      if (!ok) {
        pushToast(`无权限访问：${to.fullPath}`, 'error')
        return { name: homeRouteName }
      }
    }
  }

  return true
})
