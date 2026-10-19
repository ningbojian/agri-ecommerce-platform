// pages/personal/address/saveadd/saveadd.js
import request from '../../../../utils/req'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    name:'',
    tel:'',
    address:'',
    s:'',
    userinfo:{},
    id:''
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
  handleInputName(e){
    this.setData({
      name:e.detail.value
    })
  },
  handleInputAddress(e){
    this.setData({
      address:e.detail.value
    })
  },
  handleInputTel(e){
    this.setData({
      tel:e.detail.value
    })
  },
  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    let userinfo=wx.getStorageSync('userInfo')
    let va= wx.getStorageSync('upadd')
    this.setData({
      s:va,
      userinfo
    })
  if(va!=''){
    let add=wx.getStorageSync('addList')
    this.setData({
      name:add[va]['addname'],
      tel:add[va]['addtel'],
      address:add[va]['address'],
      id:add[va]['id']
    })
  }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },
  submit:async function(){
    let result
    if(this.data.s!=null){
      console.log(this.data.id)
      result=await request(`/admin/address/update/`,{
        userid:this.data.userinfo['id'],
        addtel:this.data.tel,
        addname:this.data.name,
        address:this.data.address,
        id:this.data.id
      })
    }
    if(this.data.s==''){
      result=await request(`/admin/address/save/`,{
        userid:this.data.userinfo['id'],
        addtel:this.data.tel,
        addname:this.data.name,
        address:this.data.address
      })
    }
    console.log(result)
    if(result.code==200){
      wx.navigateTo({
        url: '../address',
      })
    }
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