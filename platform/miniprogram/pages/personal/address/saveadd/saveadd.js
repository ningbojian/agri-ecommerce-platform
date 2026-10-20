// pages/personal/address/saveadd/saveadd.js
import request from '../../../../utils/request'
import auth from '../../../../utils/auth'

Page({
  data: {
    id: null,
    name: '',
    tel: '',
    address: '',
    isDefault: false,
    loading: false,
    submitting: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ id: Number(options.id) })
      this.loadAddress()
    }
  },

  async loadAddress() {
    if (!auth.requireLogin()) {
      return
    }
    this.setData({ loading: true })
    try {
      const address = await request(`/app/address/${this.data.id}`, {
        base: 'user',
        loading: true
      })
      this.setData({
        name: address.addname,
        tel: address.addtel,
        address: address.address,
        isDefault: address.isDefault === 1,
        loading: false
      })
    } catch (e) {
      this.setData({ loading: false })
    }
  },

  handleInputName(e) {
    this.setData({ name: e.detail.value })
  },

  handleInputTel(e) {
    this.setData({ tel: e.detail.value })
  },

  handleInputAddress(e) {
    this.setData({ address: e.detail.value })
  },

  toggleDefault(e) {
    this.setData({ isDefault: e.detail.value })
  },

  async submit() {
    const { id, name, tel, address, isDefault, submitting } = this.data
    if (submitting) {
      return
    }
    if (!name.trim()) {
      wx.showToast({ title: '请输入收货人姓名', icon: 'none' })
      return
    }
    if (!/^1[3-9]\d{9}$/.test(tel.trim()) && !/^0\d{2,3}-?\d{7,8}$/.test(tel.trim())) {
      wx.showToast({ title: '请输入正确的联系电话', icon: 'none' })
      return
    }
    if (!address.trim()) {
      wx.showToast({ title: '请输入收货地址', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    try {
      const body = {
        addname: name.trim(),
        addtel: tel.trim(),
        address: address.trim(),
        isDefault
      }
      if (id) {
        body.id = id
      }
      await request(id ? '/app/address/update' : '/app/address/save', {
        base: 'user',
        method: 'POST',
        data: body,
        loading: true
      })
      wx.showToast({ title: '已保存', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 400)
    } catch (e) {
      // request 已提示错误
    } finally {
      this.setData({ submitting: false })
    }
  }
})
