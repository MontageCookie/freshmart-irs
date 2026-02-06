export type RoleCode = 'ADMIN' | 'STORE_MANAGER' | 'WAREHOUSE' | 'CASHIER' | 'CUSTOMER'

const ADMIN_CONSOLE_ROLES = new Set<RoleCode>(['ADMIN', 'STORE_MANAGER', 'WAREHOUSE'])

export function isCashier(roles: string[] | null | undefined): boolean {
  if (!roles || roles.length === 0) return false
  return roles.includes('CASHIER')
}

export function canAccessCustomerPortal(roles: string[] | null | undefined): boolean {
  if (!roles || roles.length === 0) return false
  return roles.includes('CUSTOMER')
}

export function canAccessAdminConsole(roles: string[] | null | undefined): boolean {
  if (!roles || roles.length === 0) return false
  return roles.some((r) => ADMIN_CONSOLE_ROLES.has(r as RoleCode))
}
