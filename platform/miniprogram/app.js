import auth from './utils/auth'

// app.js
App({
  onLaunch() {
    // 清理旧版本遗留的本地数据（旧版本曾把明文密码/商品快照存进 storage）
    const legacyKeys = ['userInfo', 'flag', 'upadd', 'addList', 'pro', 'num', 'state', 'selectedAddressId']
    legacyKeys.forEach((key) => {
      try {
        wx.removeStorageSync(key)
      } catch (e) {
        // ignore
      }
    })

    // 恢复登录态，token 失效时由请求层统一处理
    const user = auth.getUser()
    if (user) {
      this.globalData.userInfo = user
    }

    const logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)
  },

  globalData: {
    userInfo: null
  }
})
