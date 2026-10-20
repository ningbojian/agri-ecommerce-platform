const TOKEN_KEY = 'xcfp_admin_token'
const ADMIN_KEY = 'xcfp_admin_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function getAdmin() {
  const raw = localStorage.getItem(ADMIN_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch (e) {
    return null
  }
}

export function setSession(token, admin) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(ADMIN_KEY, JSON.stringify(admin || {}))
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(ADMIN_KEY)
}
