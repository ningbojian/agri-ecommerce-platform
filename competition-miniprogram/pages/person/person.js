// miniprogram/pages/three/three.js

Page({

  /**
   * 页面的初始数据
   */
  data: {
   
      // newIcon: require('../.././images/ic_heart.png'),
    listData: [
      {
        'title': '全部订单',
        'icon': '../../images/ic_category.png',
        'url': '/pages/person/alllist/alllist',
      },
     
    {
      'title': '我的捐赠',
      'icon': '../../images/ic_heart.png',
      'url': '/pages/person/donation',
    },
      {
        'title': '物流查询',
        'icon': '../../images/ic_set.png',
        'url': '/pages/person/setup/setup',
      },
      {
        'title': '关于',
        'icon': '../../images/ic_about.png',
        'url': './about',
      },
      {
        'title': '意见反馈',
        'icon': '../../images/ic_opinion.png',
        'url': './feedback/feedback',
      } 
      
    ],

    listData0:[
      "1",
      "2",
      "3",
      "4",
      "5",
    ],


 
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      // cellIcon1: require('/miniprogram/images/ic_heart.png'),
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
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },
  ClickCell(event){
    var data = event.currentTarget.dataset.model;
    var text = data.title;
    console.log(text);
    var url = data.url;
    if (text == "关于" ||text == "意见反馈"|| text == "物流查询"||text == "我的捐赠"||text == "全部订单") {
      wx.navigateTo({
        url: url
      })
    }
  },
  ClickHeader(){
    wx.navigateTo({
      url: './mine'
    })
  }

})