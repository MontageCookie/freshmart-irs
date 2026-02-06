import { createRouter, createWebHistory } from 'vue-router'

import { clearSession, refreshMe, useAuthState } from '@/auth/authStore'
import { getToken } from '@/auth/token'
import { isAuthInvalidError } from '@/api/types'
import { pushToast } from '@/ui/toast'
import { canAccessAdminConsole, canAccessCustomerPortal, isCashier } from '@/auth/roleGuards'

const AdminLayout = () => import('@/layouts/admin/AdminLayout.vue')
const AdminAuthLayout = () => import('@/layouts/admin/AdminAuthLayout.vue')
const CustomerLayout = () => import('@/layouts/customer/CustomerLayout.vue')
const CustomerAuthLayout = () => import('@/layouts/customer/CustomerAuthLayout.vue')

const LoginPage = () => import('@/modules/security/pages/LoginPage.vue')
const MePage = () => import('@/modules/security/pages/MePage.vue')
const UsersPage = () => import('@/modules/security/pages/UsersPage.vue')
const UserCreatePage = () => import('@/modules/security/pages/UserCreatePage.vue')
const UserDetailPage = () => import('@/modules/security/pages/UserDetailPage.vue')
const CustomerLoginPage = () => import('@/modules/customer/pages/CustomerLoginPage.vue')
const CustomerRegisterPage = () => import('@/modules/customer/pages/CustomerRegisterPage.vue')
const CustomerMePage = () => import('@/modules/customer/pages/CustomerMePage.vue')
const CustomerProductsPage = () => import('@/modules/customer/pages/CustomerProductsPage.vue')
const CustomerProductDetailPage = () => import('@/modules/customer/pages/CustomerProductDetailPage.vue')
const ProductsPage = () => import('@/modules/catalog/pages/ProductsPage.vue')
const ProductCreatePage = () => import('@/modules/catalog/pages/ProductCreatePage.vue')
const ProductDetailPage = () => import('@/modules/catalog/pages/ProductDetailPage.vue')
const InventorySnapshotPage = () => import('@/modules/catalog/pages/InventorySnapshotPage.vue')
const InventoryLedgersPage = () => import('@/modules/catalog/pages/InventoryLedgersPage.vue')

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/manager',
      component: AdminAuthLayout,
      children: [{ path: 'login', name: 'login', component: LoginPage }],
    },
    {
      path: '/manager',
      component: AdminLayout,
      children: [
        { path: '', redirect: '/manager/me' },
        { path: 'me', name: 'me', component: MePage, meta: { requiresAuth: true } },
        {
          path: 'products',
          name: 'products',
          component: ProductsPage,
          meta: { requiresAuth: true, roles: ['STORE_MANAGER', 'WAREHOUSE'] },
        },
        {
          path: 'products/new',
          name: 'productCreate',
          component: ProductCreatePage,
          meta: { requiresAuth: true, roles: ['STORE_MANAGER'] },
        },
        {
          path: 'products/:id',
          name: 'productDetail',
          component: ProductDetailPage,
          meta: { requiresAuth: true, roles: ['STORE_MANAGER', 'WAREHOUSE'] },
        },
        {
          path: 'inventory',
          name: 'inventorySnapshot',
          component: InventorySnapshotPage,
          meta: { requiresAuth: true, roles: ['STORE_MANAGER', 'WAREHOUSE'] },
        },
        {
          path: 'inventory/ledgers',
          name: 'inventoryLedgers',
          component: InventoryLedgersPage,
          meta: { requiresAuth: true, roles: ['STORE_MANAGER', 'WAREHOUSE'] },
        },
        { path: 'users', name: 'users', component: UsersPage, meta: { requiresAuth: true, roles: ['ADMIN'] } },
        {
          path: 'users/new',
          name: 'userCreate',
          component: UserCreatePage,
          meta: { requiresAuth: true, roles: ['ADMIN'] },
        },
        {
          path: 'users/:id',
          name: 'userDetail',
          component: UserDetailPage,
          meta: { requiresAuth: true, roles: ['ADMIN'] },
        },
      ],
    },
    {
      path: '/customer',
      component: CustomerAuthLayout,
      children: [
        { path: 'login', name: 'cLogin', component: CustomerLoginPage },
        { path: 'register', name: 'cRegister', component: CustomerRegisterPage },
      ],
    },
    {
      path: '/',
      component: CustomerLayout,
      children: [
        { path: '', redirect: '/products' },
        { path: 'products', name: 'cProducts', component: CustomerProductsPage, meta: { requiresAuth: false } },
        { path: 'products/:id', name: 'cProductDetail', component: CustomerProductDetailPage, meta: { requiresAuth: false } },
        { path: 'me', name: 'cMe', component: CustomerMePage, meta: { requiresAuth: true } },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const requiresAuth = Boolean(to.meta.requiresAuth)
  const token = getToken()
  const isManagerRoute = to.path.startsWith('/manager')
  const loginRouteName = isManagerRoute ? 'login' : 'cLogin'
  const homeRouteName = isManagerRoute ? 'me' : 'cProducts'

  if (requiresAuth && !token) {
    return { name: loginRouteName, query: { redirect: to.fullPath } }
  }

  if (token && isManagerRoute) {
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
      return { path: '/products' }
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

    if (!isManagerRoute) {
      if (!canAccessCustomerPortal(authState.me?.roles)) {
        pushToast('无权限访问顾客端，请使用管理端入口', 'error')
        return { name: 'me' }
      }
    } else {
      if (!canAccessAdminConsole(authState.me?.roles)) {
        pushToast('无权限访问管理端，请使用顾客端入口', 'error')
        return { path: '/products' }
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
