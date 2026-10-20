// pages/index/index.js
import request from '../../utils/request'

Page({
  data: {
    categories: [],
    activeCategoryId: 0,
    list: [],
    current: 1,
    limit: 10,
    total: 0,
    loading: false,
    hasMore: true,
    error: ''
  },

  onLoad() {
    this.loadCategories()
    this.loadProducts(true)
  },

  async loadCategories() {
    try {
      const categories = await request('/app/product/categories', { silent: true })
      this.setData({ categories: categories || [] })
    } catch (e) {
      // 分类加载失败不阻塞商品列表
    }
  },

  async loadProducts(reset) {
    if (this.data.loading) {
      return
    }
    if (!reset && !this.data.hasMore) {
      return
    }

    const current = reset ? 1 : this.data.current
    const query = { current, limit: this.data.limit }
    if (this.data.activeCategoryId) {
      query.categoryId = this.data.activeCategoryId
    }

    this.setData({
      loading: true,
      error: reset ? '' : this.data.error
    })

    try {
      const page = await request('/app/product/page', { data: query, silent: true })
      const records = (page && page.records) || []
      const total = (page && page.total) || 0
      const list = reset ? records : this.data.list.concat(records)
      this.setData({
        list,
        total,
        current: current + 1,
        hasMore: list.length < total,
        loading: false
      })
    } catch (e) {
      this.setData({
        loading: false,
        error: e.message || '商品加载失败'
      })
    }
  },

  selectCategory(e) {
    const id = Number(e.currentTarget.dataset.id)
    if (id === this.data.activeCategoryId) {
      return
    }
    this.setData({
      activeCategoryId: id,
      list: [],
      current: 1,
      total: 0,
      hasMore: true
    })
    this.loadProducts(true)
  },

  click(e) {
    const id = e.currentTarget.dataset.id
    if (!id) {
      return
    }
    wx.navigateTo({
      url: `/pages/proinfo/proinfo?id=${id}`
    })
  },

  retry() {
    this.loadProducts(true)
  },

  onPullDownRefresh: async function () {
    await Promise.all([this.loadCategories(), this.loadProducts(true)])
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    this.loadProducts(false)
  },

  onShareAppMessage() {
    return {
      title: '乡村探宝 · 助农好物',
      path: '/pages/index/index'
    }
  }
})
