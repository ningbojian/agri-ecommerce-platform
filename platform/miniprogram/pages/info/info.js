// pages/info/info.js
import request from '../../utils/request'

Page({
  data: {
    list: [],
    loading: true,
    error: ''
  },

  onLoad() {
    this.loadArticles()
  },

  async loadArticles() {
    this.setData({ loading: true, error: '' })
    try {
      const page = await request('/app/article/page', {
        data: { current: 1, limit: 20 },
        silent: true
      })
      this.setData({
        list: (page && page.records) || [],
        loading: false
      })
    } catch (e) {
      this.setData({
        loading: false,
        error: e.message || '资讯加载失败'
      })
    }
  },

  openDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/info/detail/detail?id=${id}`
    })
  },

  retry() {
    this.loadArticles()
  },

  onPullDownRefresh: async function () {
    await this.loadArticles()
    wx.stopPullDownRefresh()
  },

  onShareAppMessage() {
    return {
      title: '乡村探宝 · 助农资讯',
      path: '/pages/info/info'
    }
  }
})
