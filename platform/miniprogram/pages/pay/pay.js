// pages/pay/pay.js
import request from '../../utils/req'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    pro:{},
    userinfo:{},
    addList:{},
    num:'',
    pay:'',
    addidx:0
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },
  choose(){
    wx.setStorageSync('flag', this.data.addidx)
    wx.navigateTo({
      url: '/pages/personal/address/address',
    })
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow:  async function () {
    let flag=wx.getStorageSync('flag')
    if(flag!=''){
      this.setData({
        addidx:flag
      })
    }
    let pro=wx.getStorageSync('pro')
    this.setData({
      pro
    })
    let userinfo=wx.getStorageSync('userInfo')
    if(userinfo.id==null){
      wx.showToast({
        title:'您还未登录'
      })
      wx.navigateTo({
        url: '/pages/login/login',
      })
      return
    }
    let result=await request(`/admin/address/findByUserId/${userinfo.id}/1/100`)
    this.setData({
      addList:result.data.records
    })
    let num=wx.getStorageSync('num')
    let pay=num*pro['price']
    this.setData({
      num,
      pay,
      userinfo
    })
  },
  order2: async function(){
    await request(`/admin/order/save`,{
      num:this.data.num,
      pay:this.data.pay,
      price:this.data.pro['price'],
      proname:this.data.pro['proname'],
      supname:this.data.pro['supname'],
      userid:this.data.userinfo['id'],
      supid:this.data.pro['supid'],
      addid:this.data.addList[this.data.addidx]['id']
    })
    wx.switchTab({
      url: '/pages/index/index',
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