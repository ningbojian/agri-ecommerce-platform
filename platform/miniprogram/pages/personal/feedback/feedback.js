import request from '../../../utils/request'
import auth from '../../../utils/auth'

Page({
  data: {
    content: '',
    contact: '',
    list: [],
    loading: true,
    submitting: false
  },

  onShow() {
    if (!auth.requireLogin()) {
      return
    }
    this.loadMine()
  },

  async loadMine() {
    this.setData({ loading: true })
    try {
      const page = await request('/app/feedback/my', {
        base: 'user',
        data: { current: 1, limit: 50 },
        silent: true
      })
      this.setData({
        list: (page && page.records) || [],
        loading: false
      })
    } catch (e) {
      this.setData({ loading: false })
    }
  },

  onInput(event) {
    this.setData({ content: event.detail.value })
  },

  onContactInput(event) {
    this.setData({ contact: event.detail.value })
  },

  async submit() {
    const { content, contact, submitting } = this.data
    if (submitting) {
      return
    }
    if (!content.trim()) {
      wx.showToast({ title: '请填写反馈内容', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    try {
      await request('/app/feedback', {
        base: 'user',
        method: 'POST',
        data: { content: content.trim(), contact: contact.trim() },
        loading: true
      })
      wx.showToast({ title: '反馈已提交', icon: 'success' })
      this.setData({ content: '', contact: '' })
      this.loadMine()
    } catch (e) {
      // request 已提示错误
    } finally {
      this.setData({ submitting: false })
    }
  },

  onPullDownRefresh: async function () {
    await this.loadMine()
    wx.stopPullDownRefresh()
  }
})
