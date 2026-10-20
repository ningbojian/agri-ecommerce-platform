import config from './config'
import auth from './auth'

let loadingCount = 0

function startLoading(title) {
  loadingCount += 1
  if (loadingCount === 1) {
    wx.showLoading({ title: title || '加载中', mask: true })
  }
}

function stopLoading() {
  loadingCount = Math.max(loadingCount - 1, 0)
  if (loadingCount === 0) {
    wx.hideLoading()
  }
}

function goLogin() {
  const pages = getCurrentPages()
  const current = pages[pages.length - 1]
  const route = current && current.route ? current.route : ''
  if (route.indexOf('pages/login/login') !== -1) {
    return
  }
  wx.navigateTo({ url: '/pages/login/login' })
}

/**
 * 统一请求封装。
 *
 * @param {string} path 接口路径，例如 /app/product/page
 * @param {object} [options]
 * @param {'product'|'user'} [options.base='product'] 请求发往哪个服务
 * @param {'GET'|'POST'|'PUT'|'DELETE'} [options.method='GET']
 * @param {object} [options.data] 查询参数或请求体
 * @param {boolean} [options.auth=false] 是否携带登录 token（有 token 时默认都会带）
 * @param {boolean} [options.loading=false] 是否展示全屏 loading
 * @param {boolean} [options.silent=false] 失败时是否不弹 toast
 * @returns {Promise<any>} 返回 Result.data
 */
export default function request(path, options = {}) {
  const {
    base = 'product',
    method = 'GET',
    data = {},
    loading = false,
    silent = false
  } = options

  const url = (base === 'user' ? config.userBase : config.productBase) + path
  const token = auth.getToken()
  const header = { 'Content-Type': 'application/json' }
  if (token) {
    header.Authorization = 'Bearer ' + token
  }

  if (loading) {
    startLoading()
  }

  return new Promise((resolve, reject) => {
    wx.request({
      url,
      method,
      data,
      header,
      success(res) {
        const payload = res.data
        const code = payload && payload.code

        if (res.statusCode === 401 || code === 208) {
          auth.clear()
          if (!silent) {
            wx.showToast({ title: '登录已失效，请重新登录', icon: 'none' })
          }
          goLogin()
          reject(new Error('未登录'))
          return
        }

        if (res.statusCode === 403 || code === 209) {
          if (!silent) {
            wx.showToast({ title: (payload && payload.message) || '没有权限', icon: 'none' })
          }
          reject(new Error('没有权限'))
          return
        }

        if (res.statusCode >= 200 && res.statusCode < 300 && code === 200) {
          resolve(payload.data)
          return
        }

        const message = (payload && payload.message) || `请求失败（${res.statusCode}）`
        if (!silent) {
          wx.showToast({ title: message, icon: 'none' })
        }
        reject(new Error(message))
      },
      fail(err) {
        if (!silent) {
          wx.showToast({ title: '无法连接服务器，请确认后端已启动', icon: 'none' })
        }
        reject(err)
      },
      complete() {
        if (loading) {
          stopLoading()
        }
      }
    })
  })
}
