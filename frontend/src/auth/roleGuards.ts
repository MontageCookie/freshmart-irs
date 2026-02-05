export type RoleCode = 'ADMIN' | 'STORE_MANAGER' | 'WAREHOUSE' | 'CASHIER' | 'CUSTOMER'

const STAFF_ROLES = new Set<RoleCode>(['ADMIN', 'STORE_MANAGER', 'WAREHOUSE', 'CASHIER'])

export function canAccessCustomerPortal(roles: string[] | null | undefined): boolean {
  if (!roles || roles.length === 0) return false
  return roles.includes('CUSTOMER')
}

export function canAccessAdminConsole(roles: string[] | null | undefined): boolean {
  if (!roles || roles.length === 0) return false
  return roles.some((r) => STAFF_ROLES.has(r as RoleCode))
}
