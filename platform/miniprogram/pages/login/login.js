// pages/login/login.js
import request from '../../utils/request'
import auth from '../../utils/auth'

Page({
  data: {
    phone: '',
    password: '',
    submitting: false
  },

  handleInput(event) {
    const type = event.currentTarget.id
    this.setData({
      [type]: event.detail.value
    })
  },

  async login() {
    const { phone, password, submitting } = this.data
    if (submitting) {
      return
    }
    if (!phone) {
      wx.showToast({ title: '手机号不能为空', icon: 'none' })
      return
    }
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '手机号格式不正确', icon: 'none' })
      return
    }
    if (!password) {
      wx.showToast({ title: '密码不能为空', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    try {
      const result = await request('/app/auth/login', {
        base: 'user',
        method: 'POST',
        data: { tel: phone, password },
        loading: true
      })
      auth.setLogin(result.token, result.user)
      const app = getApp()
      if (app) {
        app.globalData.userInfo = result.user
      }
      wx.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        const pages = getCurrentPages()
        if (pages.length > 1) {
          wx.navigateBack()
        } else {
          wx.reLaunch({ url: '/pages/personal/personal' })
        }
      }, 400)
    } catch (e) {
      // request 已提示错误
    } finally {
      this.setData({ submitting: false })
    }
  },

  toRegister() {
    wx.navigateTo({ url: '/pages/register/register' })
  }
})
