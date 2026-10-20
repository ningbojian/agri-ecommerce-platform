// pages/register/register.js
import request from '../../utils/request'
import auth from '../../utils/auth'

Page({
  data: {
    phone: '',
    nickname: '',
    password: '',
    repass: '',
    submitting: false
  },

  handleInput(event) {
    const type = event.currentTarget.id
    this.setData({
      [type]: event.detail.value
    })
  },

  async register() {
    const { phone, nickname, password, repass, submitting } = this.data
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
    if (!password || password.length < 6) {
      wx.showToast({ title: '密码至少 6 位', icon: 'none' })
      return
    }
    if (password !== repass) {
      wx.showToast({ title: '两次密码输入不一致', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    try {
      const result = await request('/app/auth/register', {
        base: 'user',
        method: 'POST',
        data: { tel: phone, password, nickname },
        loading: true
      })
      auth.setLogin(result.token, result.user)
      wx.showToast({ title: '注册成功', icon: 'success' })
      setTimeout(() => {
        wx.reLaunch({ url: '/pages/personal/personal' })
      }, 500)
    } catch (e) {
      // request 已提示错误
    } finally {
      this.setData({ submitting: false })
    }
  },

  toLogin() {
    wx.navigateBack()
  }
})
