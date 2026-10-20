// pages/personal/order/detail/detail.js
import request from '../../../../utils/request'
import auth from '../../../../utils/auth'

Page({
  data: {
    orderid: '',
    order: null,
    loading: true,
    error: ''
  },

  onLoad(options) {
    if (!options.orderid) {
      this.setData({ loading: false, error: '缺少订单号' })
      return
    }
    this.setData({ orderid: options.orderid })
  },

  onShow() {
    if (!auth.requireLogin()) {
      return
    }
    this.loadOrder()
  },

  async loadOrder() {
    this.setData({ loading: true, error: '' })
    try {
      const order = await request(`/app/order/${this.data.orderid}`, {
        base: 'user',
        silent: true
      })
      this.setData({ order, loading: false })
    } catch (e) {
      this.setData({ loading: false, error: e.message || '订单加载失败' })
    }
  },

  async pay() {
    try {
      await request(`/app/order/${this.data.orderid}/pay`, {
        base: 'user',
        method: 'POST',
        loading: true
      })
      wx.showToast({ title: '支付成功', icon: 'success' })
      this.loadOrder()
    } catch (e) {
      // request 已提示错误
    }
  },

  cancel() {
    wx.showModal({
      title: '取消订单',
      content: '取消后库存会自动恢复，确定取消吗？',
      success: async (res) => {
        if (!res.confirm) {
          return
        }
        try {
          await request(`/app/order/${this.data.orderid}/cancel`, {
            base: 'user',
            method: 'POST',
            loading: true
          })
          wx.showToast({ title: '已取消', icon: 'success' })
          this.loadOrder()
        } catch (e) {
          // request 已提示错误
        }
      }
    })
  },

  confirm() {
    wx.showModal({
      title: '确认收货',
      content: '请确认已收到商品，确认后订单完成。',
      success: async (res) => {
        if (!res.confirm) {
          return
        }
        try {
          await request(`/app/order/${this.data.orderid}/confirm`, {
            base: 'user',
            method: 'POST',
            loading: true
          })
          wx.showToast({ title: '已确认收货', icon: 'success' })
          this.loadOrder()
        } catch (e) {
          // request 已提示错误
        }
      }
    })
  }
})
