// pages/personal/personal.js
import request from '../../utils/request'
import auth from '../../utils/auth'

Page({
  data: {
    userInfo: null,
    loggedIn: false
  },

  onShow() {
    this.refreshProfile()
  },

  async refreshProfile() {
    if (!auth.isLoggedIn()) {
      this.setData({ loggedIn: false, userInfo: null })
      return
    }

    // 先用本地缓存渲染，再向后端刷新
    this.setData({ loggedIn: true, userInfo: auth.getUser() })
    try {
      const user = await request('/app/user/profile', { base: 'user', silent: true })
      auth.updateUser(user)
      this.setData({ userInfo: user })
      const app = getApp()
      if (app) {
        app.globalData.userInfo = user
      }
    } catch (e) {
      if (!auth.isLoggedIn()) {
        this.setData({ loggedIn: false, userInfo: null })
      }
    }
  },

  toLogin() {
    if (this.data.loggedIn) {
      return
    }
    wx.navigateTo({ url: '/pages/login/login' })
  },

  goOrders(e) {
    if (!auth.requireLogin()) {
      return
    }
    const state = e.currentTarget.dataset.state || ''
    wx.navigateTo({
      url: `/pages/personal/order/order?state=${state}`
    })
  },

  goAddress() {
    if (!auth.requireLogin()) {
      return
    }
    wx.navigateTo({ url: '/pages/personal/address/address' })
  },

  goFeedback() {
    if (!auth.requireLogin()) {
      return
    }
    wx.navigateTo({ url: '/pages/personal/feedback/feedback' })
  },

  logout() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出当前账号吗？',
      success: (res) => {
        if (!res.confirm) {
          return
        }
        auth.clear()
        const app = getApp()
        if (app) {
          app.globalData.userInfo = null
        }
        this.setData({ loggedIn: false, userInfo: null })
        wx.showToast({ title: '已退出登录', icon: 'none' })
      }
    })
  }
})
