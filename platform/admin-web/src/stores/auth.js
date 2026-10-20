import { defineStore } from 'pinia'
import { adminLogin } from '../api/user'
import { clearSession, getAdmin, getToken, setSession } from '../utils/session'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    admin: getAdmin()
  }),
  getters: {
    isLoggedIn: state => Boolean(state.token),
    displayName: state => (state.admin && (state.admin.nickname || state.admin.username)) || '管理员'
  },
  actions: {
    async login(form) {
      const data = await adminLogin(form)
      this.token = data.token
      this.admin = data.admin
      setSession(data.token, data.admin)
      return data
    },
    logout() {
      clearSession()
      this.token = ''
      this.admin = null
    }
  }
})
