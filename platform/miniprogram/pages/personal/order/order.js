// pages/personal/order/order.js
import request from '../../../utils/request'
import auth from '../../../utils/auth'

const TABS = [
  { label: '全部', value: '' },
  { label: '待付款', value: 'created' },
  { label: '待发货', value: 'paid' },
  { label: '待收货', value: 'shipped' },
  { label: '已完成', value: 'completed' }
]

const VALID_STATES = TABS.map((item) => item.value)

Page({
  data: {
    tabs: TABS,
    activeState: '',
    list: [],
    current: 1,
    limit: 10,
    total: 0,
    loading: false,
    hasMore: true,
    error: ''
  },

  onLoad(options) {
    const state = VALID_STATES.indexOf(options.state) >= 0 ? options.state : ''
    this.setData({ activeState: state })
  },

  onShow() {
    if (!auth.requireLogin()) {
      return
    }
    this.loadOrders(true)
  },

  async loadOrders(reset) {
    if (this.data.loading) {
      return
    }
    if (!reset && !this.data.hasMore) {
      return
    }

    const current = reset ? 1 : this.data.current
    const query = { current, limit: this.data.limit }
    if (this.data.activeState) {
      query.state = this.data.activeState
    }

    this.setData({ loading: true, error: '' })
    try {
      const page = await request('/app/order/page', {
        base: 'user',
        data: query,
        silent: true
      })
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
        error: e.message || '订单加载失败'
      })
    }
  },

  switchTab(e) {
    const state = e.currentTarget.dataset.state
    if (state === this.data.activeState) {
      return
    }
    this.setData({
      activeState: state,
      list: [],
      current: 1,
      total: 0,
      hasMore: true
    })
    this.loadOrders(true)
  },

  noop() {},

  openDetail(e) {
    const orderid = e.currentTarget.dataset.orderid
    wx.navigateTo({
      url: `/pages/personal/order/detail/detail?orderid=${orderid}`
    })
  },

  async pay(e) {
    const orderid = e.currentTarget.dataset.orderid
    try {
      await request(`/app/order/${orderid}/pay`, {
        base: 'user',
        method: 'POST',
        loading: true
      })
      wx.showToast({ title: '支付成功', icon: 'success' })
      this.loadOrders(true)
    } catch (err) {
      // request 已提示错误
    }
  },

  cancel(e) {
    const orderid = e.currentTarget.dataset.orderid
    wx.showModal({
      title: '取消订单',
      content: '取消后库存会自动恢复，确定取消吗？',
      success: async (res) => {
        if (!res.confirm) {
          return
        }
        try {
          await request(`/app/order/${orderid}/cancel`, {
            base: 'user',
            method: 'POST',
            loading: true
          })
          wx.showToast({ title: '已取消', icon: 'success' })
          this.loadOrders(true)
        } catch (err) {
          // request 已提示错误
        }
      }
    })
  },

  confirm(e) {
    const orderid = e.currentTarget.dataset.orderid
    wx.showModal({
      title: '确认收货',
      content: '请确认已收到商品，确认后订单完成。',
      success: async (res) => {
        if (!res.confirm) {
          return
        }
        try {
          await request(`/app/order/${orderid}/confirm`, {
            base: 'user',
            method: 'POST',
            loading: true
          })
          wx.showToast({ title: '已确认收货', icon: 'success' })
          this.loadOrders(true)
        } catch (err) {
          // request 已提示错误
        }
      }
    })
  },

  retry() {
    this.loadOrders(true)
  },

  onPullDownRefresh: async function () {
    await this.loadOrders(true)
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    this.loadOrders(false)
  }
})
