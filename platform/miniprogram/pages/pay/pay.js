// pages/pay/pay.js
import request from '../../utils/request'
import auth from '../../utils/auth'

const SELECTED_ADDRESS_KEY = 'xcfp_selected_address_id'

Page({
  data: {
    productId: null,
    num: 1,
    pro: null,
    addresses: [],
    address: null,
    pay: '0.00',
    loading: true,
    submitting: false,
    error: ''
  },

  onLoad(options) {
    const productId = Number(options.productId)
    const num = Math.max(Number(options.num) || 1, 1)
    this.setData({ productId, num })
  },

  onShow() {
    if (!auth.isLoggedIn()) {
      auth.requireLogin()
      return
    }
    this.loadData()
  },

  async loadData() {
    this.setData({ loading: true, error: '' })
    try {
      const [pro, addresses] = await Promise.all([
        request(`/app/product/${this.data.productId}`, { silent: true }),
        request('/app/address/list', { base: 'user', silent: true })
      ])

      const list = addresses || []
      const selectedId = Number(wx.getStorageSync(SELECTED_ADDRESS_KEY) || 0)
      wx.removeStorageSync(SELECTED_ADDRESS_KEY)

      let address = null
      if (selectedId) {
        address = list.find((item) => item.id === selectedId) || null
      }
      if (!address) {
        address = list.find((item) => item.isDefault === 1) || list[0] || null
      }

      const num = Math.min(this.data.num, pro.stock || 0)
      const pay = (Number(pro.price) * num).toFixed(2)

      this.setData({
        pro,
        num: num || this.data.num,
        addresses: list,
        address,
        pay,
        loading: false
      })
    } catch (e) {
      this.setData({
        loading: false,
        error: e.message || '结算信息加载失败'
      })
    }
  },

  chooseAddress() {
    if (!this.data.addresses.length) {
      this.addAddress()
      return
    }
    wx.navigateTo({
      url: '/pages/personal/address/address?select=1'
    })
  },

  addAddress() {
    wx.navigateTo({
      url: '/pages/personal/address/saveadd/saveadd'
    })
  },

  async submit() {
    if (this.data.submitting) {
      return
    }
    const { pro, address, num } = this.data
    if (!pro) {
      return
    }
    if (!address) {
      wx.showToast({ title: '请先选择收货地址', icon: 'none' })
      return
    }
    if (!pro.stock || pro.stock < num) {
      wx.showToast({ title: '库存不足', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    let order = null
    try {
      order = await request('/app/order/create', {
        base: 'user',
        method: 'POST',
        data: { productId: pro.id, num, addressId: address.id },
        loading: true
      })
    } catch (e) {
      this.setData({ submitting: false })
      return
    }

    try {
      await request(`/app/order/${order.orderid}/pay`, {
        base: 'user',
        method: 'POST',
        loading: true
      })
      wx.showModal({
        title: '支付成功',
        content: '当前为演示用模拟支付，不会产生真实扣款。',
        showCancel: false,
        success: () => {
          wx.redirectTo({
            url: '/pages/personal/order/order?state=paid'
          })
        }
      })
    } catch (e) {
      wx.showModal({
        title: '订单已创建',
        content: '订单已生成但支付未完成，可在“我的订单”中继续支付。',
        showCancel: false,
        success: () => {
          wx.redirectTo({
            url: '/pages/personal/order/order?state=created'
          })
        }
      })
    } finally {
      this.setData({ submitting: false })
    }
  }
})
