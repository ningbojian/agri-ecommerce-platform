// pages/personal/address/address.js
import request from '../../../utils/request'
import auth from '../../../utils/auth'

const SELECTED_ADDRESS_KEY = 'xcfp_selected_address_id'

Page({
  data: {
    list: [],
    loading: true,
    error: '',
    selectMode: false
  },

  onLoad(options) {
    this.setData({
      selectMode: options.select === '1'
    })
  },

  onShow() {
    if (!auth.requireLogin()) {
      return
    }
    this.loadList()
  },

  async loadList() {
    this.setData({ loading: true, error: '' })
    try {
      const list = await request('/app/address/list', { base: 'user', silent: true })
      this.setData({ list: list || [], loading: false })
    } catch (e) {
      this.setData({ loading: false, error: e.message || '地址加载失败' })
    }
  },

  add() {
    wx.navigateTo({ url: '/pages/personal/address/saveadd/saveadd' })
  },

  edit(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/personal/address/saveadd/saveadd?id=${id}` })
  },

  choose(e) {
    if (!this.data.selectMode) {
      return
    }
    const id = e.currentTarget.dataset.id
    wx.setStorageSync(SELECTED_ADDRESS_KEY, id)
    wx.navigateBack()
  },

  remove(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '删除地址',
      content: '确定删除这条收货地址吗？',
      success: async (res) => {
        if (!res.confirm) {
          return
        }
        try {
          await request(`/app/address/${id}`, {
            base: 'user',
            method: 'DELETE',
            loading: true
          })
          wx.showToast({ title: '已删除', icon: 'success' })
          this.loadList()
        } catch (e) {
          // request 已提示错误
        }
      }
    })
  },

  async setDefault(e) {
    const id = e.currentTarget.dataset.id
    try {
      await request(`/app/address/${id}/default`, {
        base: 'user',
        method: 'POST',
        loading: true
      })
      wx.showToast({ title: '已设为默认', icon: 'success' })
      this.loadList()
    } catch (e) {
      // request 已提示错误
    }
  },

  onPullDownRefresh: async function () {
    await this.loadList()
    wx.stopPullDownRefresh()
  }
})
