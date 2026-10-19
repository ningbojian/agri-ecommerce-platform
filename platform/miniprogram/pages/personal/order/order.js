// pages/personal/order/order.js
import request from '../../../utils/req'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userid:'',
    state:'',
    orderList:{}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: async function () {
    let v=wx.getStorageSync('state')
    let va=wx.getStorageSync('userInfo')
    this.setData({
      state:v,
      userid:va.id
    })
    let result=await request(`/admin/order/findByUserId/${this.data.userid}`,{
      state:this.data.state
    })
    this.setData({
      orderList:result.data.records
    })
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})