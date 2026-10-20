// pages/proinfo/proinfo.js
import request from '../../utils/request'
import auth from '../../utils/auth'

Page({
  data: {
    id: null,
    pro: null,
    num: 1,
    minusStatus: 'disabled',
    loading: true,
    error: ''
  },

  onLoad(options) {
    const id = Number(options.id)
    if (!id) {
      this.setData({ loading: false, error: '缺少商品参数' })
      return
    }
    this.setData({ id })
    this.loadProduct()
  },

  async loadProduct() {
    this.setData({ loading: true, error: '' })
    try {
      const pro = await request(`/app/product/${this.data.id}`, { silent: true })
      this.setData({ pro, loading: false })
    } catch (e) {
      this.setData({ loading: false, error: e.message || '商品加载失败' })
    }
  },

  bindMinus() {
    const num = this.data.num
    if (num <= 1) {
      return
    }
    const next = num - 1
    this.setData({
      num: next,
      minusStatus: next <= 1 ? 'disabled' : 'normal'
    })
  },

  bindPlus() {
    const pro = this.data.pro
    if (!pro) {
      return
    }
    const max = Math.min(pro.stock || 0, 99)
    if (this.data.num >= max) {
      wx.showToast({ title: max ? '已达可购买上限' : '库存不足', icon: 'none' })
      return
    }
    this.setData({
      num: this.data.num + 1,
      minusStatus: 'normal'
    })
  },

  bindManual(e) {
    const pro = this.data.pro
    const max = pro ? Math.min(pro.stock || 0, 99) : 1
    let num = parseInt(e.detail.value, 10)
    if (isNaN(num) || num < 1) {
      num = 1
    }
    if (num > max) {
      num = max || 1
    }
    this.setData({
      num,
      minusStatus: num <= 1 ? 'disabled' : 'normal'
    })
  },

  pay() {
    const pro = this.data.pro
    if (!pro) {
      return
    }
    if (!auth.requireLogin()) {
      return
    }
    if (!pro.stock || pro.stock < this.data.num) {
      wx.showToast({ title: '库存不足', icon: 'none' })
      return
    }
    wx.navigateTo({
      url: `/pages/pay/pay?productId=${pro.id}&num=${this.data.num}`
    })
  },

  onPullDownRefresh: async function () {
    await this.loadProduct()
    wx.stopPullDownRefresh()
  },

  onShareAppMessage() {
    const pro = this.data.pro
    return {
      title: pro ? pro.proname : '乡村探宝',
      path: `/pages/proinfo/proinfo?id=${this.data.id}`
    }
  }
})
