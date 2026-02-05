const TOKEN_KEY = 'freshmart_irs_access_token'

export function getToken(): string | null {
  const raw = localStorage.getItem(TOKEN_KEY)
  return raw && raw.trim().length > 0 ? raw : null
}

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}
