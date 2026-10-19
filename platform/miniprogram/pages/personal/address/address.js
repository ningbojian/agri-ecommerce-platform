// pages/personal/address/address.js
import request from '../../../utils/req'
import req from '../../../utils/reqs'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userid:'',
    current:1,
    limit:100,
    addList:{},
    flag:''
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
  save(){
    wx.setStorageSync('upadd','')
    wx.navigateTo({
      url: '../address/saveadd/saveadd',
    })
  },
  update(e){
    wx.setStorageSync('upadd',e.currentTarget.id)
    wx.navigateTo({
      url: '../address/saveadd/saveadd',
    })
  },
  /**
   * 生命周期函数--监听页面显示
   */
  onShow: async function () {
    let flag=wx.getStorageSync('flag')
    let va=wx.getStorageSync('userInfo')
    if(flag!=-1){
      this.setData({
        flag:flag+1
      })
    }
    this.setData({
      userid:va.id,
    })
    let result=await request(`/admin/address/findByUserId/${this.data.userid}/${this.data.current}/${this.data.limit}`)
    this.setData({
      addList:result.data.records
    })
    wx.setStorageSync('addList', this.data.addList)
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
  remove: async function(e){
    await req(`/admin/address/removeById/${this.data.addList[e.currentTarget.id]['id']}`)
    this.onShow()
  },
  submit(e){
    wx.setStorageSync('flag',e.currentTarget.id)
    wx.navigateBack({
      delta: 0,
    })
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