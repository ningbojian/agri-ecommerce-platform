// pages/login/login.js
import request from '../../utils/req'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    phone:'',
    password:'',
    repass:''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },
  // 表单项内容发生改变的回调
  handleInput(event){
    let type=event.currentTarget.id;
    // console.log(type,event.detail.value);
    this.setData({
      [type]:event.detail.value
    })
  },
  // 登录的回调
  async login(){
    let {phone,password}=this.data;
    //前端验证
    if(!phone){
      wx.showToast({
        title:'手机号不能为空',
        icon:'none'
      })
      return;
    }
    //定义正则表达式
    let phoneReg=/^1(3|4|5|6|7|8|9)\d{9}$/;
    if(!phoneReg.test(phone)){
      wx.showToast({
        title:'手机号格式不正确',
        icon:'none'
      })
      return;
    }
    if(this.data.password!=this.data.repass){
      wx.showToast({
        title:'两次密码输入不一致',
        icon:'none'
      })
      return;
    }
    if(!password){
      wx.showToast({
        title:'密码不能为空',
        icon:'none'
      })
      return;
    }
    //后端验证
    let re=await request(`/admin/user/findByTel/${phone}`)
    if(re.data!=null){
      wx.showToast({
        title:'手机号已被注册',
        icon:'none'
      })
      return;
    }
    let result=await request(`/admin/user/saveUser`,{
      tel:phone,
      password:password
    })
    if(result.code==200){
      wx.navigateBack({
        delta: 0,
      })
    }
  },
  register(){
    wx.navigateTo({
      url: '/pages/register/register',
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