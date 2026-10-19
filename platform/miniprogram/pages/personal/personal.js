// pages/personal/personal.js
let startY=0;//手指启示坐标
let moveY=0;//手指移动坐标
let movuDistance=0;//手指移动距离
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo:{}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    
  },
  toLogin(){
    if(this.data.userInfo.tel){
      return
    }
    wx.navigateTo({
      url:"/pages/login/login"
    })
  },
  click1(){
    wx.setStorageSync('state', '')
    wx.navigateTo({
      url: './order/order',
    })
  },
  click2(){
    wx.setStorageSync('state', '0')
    wx.navigateTo({
      url: './order/order',
    })
  },
  click3(){
    wx.setStorageSync('state', '1')
    wx.navigateTo({
      url: './order/order',
    })
  },
  click4(){
    wx.setStorageSync('state', '2')
    wx.navigateTo({
      url: './order/order',
    })
  },
  handleTouchStart(event){
    this.setData({
      coverTransition:''
    })
    //获取手指启示坐标
    startY=event.touches[0].clientY;
  },
  handleTouchMove(event){
    //获取手指移动坐标
    moveY=event.touches[0].clientY;
    movuDistance=moveY-startY;
    if(movuDistance<=0){
      return;
    }
    if(movuDistance>=80){
      movuDistance=80;
    }
    // 动态更新coverTransform的状态值
    this.setData({
      coverTransform:`translateY(${movuDistance}rpx)`
    })
  },
  handleTouchEnd(){
    this.setData({
      coverTransform:`translateY(0rpx)`,
      coverTransition:'transform 1s linear'
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
  onShow: function () {
    let va=wx.getStorageSync('userInfo')
    this.setData({
      userInfo:va
    })
    wx.setStorageSync('flag',-1)
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