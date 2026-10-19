// pages/index/index.js
import request from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    current:1,
    limit:10,
    sort:'',
    list:{}
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
    let date= await request(`/admin/pro/productinfo/findPage/${this.data.current}/${this.data.limit}`,{
      sort:this.data.sort
    })
    this.setData({
      list:date.data.records
    })
  },
  click(e){
    wx.setStorageSync('pro',this.data.list[e.target.id])
    // console.log(this.data.list[e.target.id]['id'])
    wx.navigateTo({
      url: '/pages/proinfo/proinfo',
    })
  },
  sort(e){
    this.setData({
      sort:e.currentTarget.id
    })
    this.onShow()
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