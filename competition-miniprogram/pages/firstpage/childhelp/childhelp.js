const db = wx.cloud.database()    //这一句必不可少
Page({
 
  /**
   * 页面的初始数据
   */
  data: {
    username: '', //姓名
    mobile: '', //手机号
    product: '', //捐赠物品
    address: '', //捐赠目的地
  },
  // 用户名失去焦点
  username(e) {
    this.setData({
      username: e.detail.value
    })
  },
  // 手机号失去焦点
  mobile(e) {
    this.setData({
      mobile: e.detail.value
    })
  },
  // 密码失去焦点
  product(e) {
      this.setData({
        product: e.detail.value
      })
    
  },
  // 确认密码失去焦点
  address(e) {
    this.setData({
      address: e.detail.value
    })
  },
  // 点击注册按钮
  bindGetUserInfo: function(e){
    if (e.detail.userInfo) {
      //用户按了允许授权按钮
        wx.showLoading({
          title: '正在提交',
        })
        if (this.data.username == '') {
          wx.showToast({
            title: '姓名不能为空',
            icon: 'none',
            duration: 2000
          })
        } else if (this.data.mobile == '') {
          wx.showToast({
            title: '手机号不能为空',
            icon: 'none',
            duration: 2000
          })
        } else if (!(/^1[3456789]\d{9}$/.test(this.data.mobile))) {
          wx.showToast({
            title: '手机号码格式有误，请重新输入！',
            icon: 'none',
            duration: 2000
          })
        } else if (this.data.product == '') {
          wx.showToast({
            title: '请输入捐赠物品',
            icon: 'none',
            duration: 2000
          })
        } else if (this.data.address == '') {
          wx.showToast({
            title: '请输入地址',
            icon: 'none',
            duration: 2000
          })
        } else {
          var that = this
          // 注册这个账户之前，我们首先要做的就是查询一下这个集合表中是否已经存在过这个用户了
          db.collection('childhelp').where({ //查询接口
              username: that.data.username //传参，输入的用户名
            })
        .get({
              success: function(res) {
                if (res.data.length == 0) { //判断用户名是否被注册过，等于空说明没被查询到，就是没有注册过，
                  db.collection('childhelp').where({ //我写的数用户名和手机号都可以登录，所以同一个手机号和用户名只能算一个账号，也要验证一下手机号是否被注册过
                      mobile: that.data.mobile
                    })
                    .get({
                      success: function(res) {
                        if (res.data.length == 0) { //判断手机号是否被注册过，等于空说明没被查询到，就是没有注册过，
                          // 获取当前时间，写入数据库，可以知道此账号是何时创建
                          var myDate = new Date();
                          var y = myDate.getFullYear();
                          var m = myDate.getMonth() + 1;
                          var d = myDate.getDate();
                          var h = myDate.getHours();
                          var ms = myDate.getMinutes();
                          var s = myDate.getSeconds();
                          var time = y + '-' + m + '-' + d + ' ' + h + ':' + ms + ':' + s
                          db.collection('childhelp').add({ //验证完成之后，添加的接口
                            data: {
                              username: that.data.username, //用户名
                              mobile: that.data.mobile, //手机号
                              product: that.data.product, //密码
                              address: that.data.address, //密码
                              time: time //创建时间
                            },
                            success: function(res) {
                              if (res.errMsg == 'collection.add:ok') { //接口调取成功，也就是注册成功
                                // 这里面你可以加一下注册成功之后的逻辑，是直接登录也好，或者是跳到登录页面页面
                                wx.hideLoading();
                                wx.showToast({
                                  title: '提交成功',
                                  icon: 'none'
                                })
                              }
                            }
                          })
                        } 
                      }
                    })
                } 
              }
            })
          }
      } else {
        //用户按了拒绝按钮
          wx.showModal({
            title: '警告',
            content: '您点击了拒绝授权，将无法进行提交，请授权之后再提交!!!',
            showCancel: false,
            confirmText: '返回授权',
            success: function(res) {}
          })
      }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    
  },
 
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {
 
  },
 
  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {
 
  },
 
  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {
 
  },
 
  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {
 
  },
 
  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {
 
  },
 
  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {
 
  },
 
  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {
 
  }
})