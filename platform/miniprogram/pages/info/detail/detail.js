// pages/info/detail/detail.js
import request from '../../../utils/request'

Page({
  data: {
    id: null,
    article: null,
    loading: true,
    error: ''
  },

  onLoad(options) {
    const id = Number(options.id)
    if (!id) {
      this.setData({ loading: false, error: '缺少资讯参数' })
      return
    }
    this.setData({ id })
    this.loadArticle()
  },

  async loadArticle() {
    this.setData({ loading: true, error: '' })
    try {
      const article = await request(`/app/article/${this.data.id}`, { silent: true })
      this.setData({ article, loading: false })
    } catch (e) {
      this.setData({ loading: false, error: e.message || '资讯加载失败' })
    }
  },

  onPullDownRefresh: async function () {
    await this.loadArticle()
    wx.stopPullDownRefresh()
  },

  onShareAppMessage() {
    const article = this.data.article
    return {
      title: article ? article.title : '乡村探宝资讯',
      path: `/pages/info/detail/detail?id=${this.data.id}`
    }
  }
})
