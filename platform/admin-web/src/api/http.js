import axios from 'axios'
import { ElMessage } from 'element-plus'
import { clearSession, getToken } from '../utils/session'

function goLogin() {
  clearSession()
  if (window.location.hash !== '#/login') {
    ElMessage.warning('登录已过期，请重新登录')
    window.location.hash = '#/login'
  }
}

function createClient(baseURL) {
  const client = axios.create({ baseURL, timeout: 20000 })

  client.interceptors.request.use(config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })

  client.interceptors.response.use(
    response => {
      const body = response.data
      if (body && typeof body === 'object' && 'code' in body) {
        if (body.code === 200) return body.data
        if (body.code === 208) {
          goLogin()
          return Promise.reject(new Error(body.message || '登录已过期'))
        }
        const message = body.message || '请求失败'
        ElMessage.error(message)
        return Promise.reject(new Error(message))
      }
      return body
    },
    error => {
      const status = error.response && error.response.status
      const body = error.response && error.response.data
      if (status === 401 || (body && body.code === 208)) {
        goLogin()
        return Promise.reject(new Error((body && body.message) || '登录已过期'))
      }
      const message =
        (body && body.message) || (status === 403 ? '没有操作权限' : error.message) || '网络异常'
      ElMessage.error(message)
      return Promise.reject(new Error(message))
    }
  )

  return client
}

export const proHttp = createClient('/pro-api')
export const userHttp = createClient('/user-api')
export const supHttp = createClient('/sup-api')
