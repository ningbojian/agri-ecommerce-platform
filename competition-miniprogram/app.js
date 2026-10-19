//app.js  var app = getApp();
App({

  //全局数据
  globalData: {
    name: '张三',
    isIPhoneX: false,
  },
  //全局宏
  kUserInfo: "kUserInfo",
  isIPhoneX: false, // 当前设备是否为 iPhone X
  kBottomSafeHeight: 0, // X 34 ，其余 0 

  onLaunch: function () {
    wx.cloud.init({
       env: "YOUR_CLOUD_ENV_ID",
      traceUser: true,
    })

    this.globalData = {}
      //数据库
      const db = wx.cloud.database({
        env: "YOUR_CLOUD_ENV_ID"
      });
      //数据库
    this.checkIsIPhoneX()
  },


  // 判断设备是否为 iPhone X
  checkIsIPhoneX: function () {
    const that = this
    wx.getSystemInfo({
      success: function (res) {
        var safeBottom = res.screenHeight - res.safeArea.bottom
        that.kBottomSafeHeight = safeBottom
        //根据安全高度判断
        if (safeBottom === 34) {
          that.globalData.isIPhoneX = true
          that.isIPhoneX = true
        }
        // // 根据 model 进行判断
        // if (res.model.search('iPhone X') != -1) {
        //   that.globalData.isIPhoneX = true
        //   that.isIPhoneX = true
        // }
        // // 或者根据 screenHeight 进行判断
        // if (res.screenHeight == 812 || res.screenHeight == 896) {
        //   that.isIPhoneX = true
        // }
      }
    })
  },
})