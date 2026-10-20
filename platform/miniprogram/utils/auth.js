const TOKEN_KEY = 'xcfp_token'
const USER_KEY = 'xcfp_user'

function getToken() {
  return wx.getStorageSync(TOKEN_KEY) || ''
}

function getUser() {
  return wx.getStorageSync(USER_KEY) || null
}

function setLogin(token, user) {
  wx.setStorageSync(TOKEN_KEY, token)
  wx.setStorageSync(USER_KEY, user || null)
}

function updateUser(user) {
  wx.setStorageSync(USER_KEY, user || null)
}

function clear() {
  wx.removeStorageSync(TOKEN_KEY)
  wx.removeStorageSync(USER_KEY)
}

function isLoggedIn() {
  return Boolean(getToken())
}

/**
 * 登录守卫：未登录时提示并跳转登录页。
 * @returns {boolean} 是否已登录
 */
function requireLogin(message) {
  if (isLoggedIn()) {
    return true
  }
  wx.showToast({ title: message || '请先登录', icon: 'none' })
  wx.navigateTo({ url: '/pages/login/login' })
  return false
}

export default {
  TOKEN_KEY,
  USER_KEY,
  getToken,
  getUser,
  setLogin,
  updateUser,
  clear,
  isLoggedIn,
  requireLogin
}
