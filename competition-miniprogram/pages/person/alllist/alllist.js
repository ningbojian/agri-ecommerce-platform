

Page({

  /**
   * 页面的初始数据
   */
  data: {
   
      // newIcon: require('../.././images/ic_heart.png'),
    listData: [
      {
        'title': '全部',
        'icon': '../../../images/ic_category.png',
        'url': '/pages/person/alllist/a',
      },
     
    {
      'title': '待付款',
      'icon': '../../../images/ic_heart.png',
      'url': '/pages/person/alllist/b',
    },
      {
        'title': '待发货',
        'icon': '../../../images/ic_set.png',
        'url': '/pages/person/alllist/c',
      },
      {
        'title': '退款/退货',
        'icon': '../../../images/ic_about.png',
        'url': '/pages/person/alllist/d',
      },
      
      
    ],

    listData0:[
      "1",
      "2",
      "3",
      "4",
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
    if (text == "全部" ||text == "待付款"|| text == "待发货"||text == "退款/退货") {
      wx.navigateTo({
        url: url
      })
    }
  },
  ClickHeader(){
    wx.navigateTo({
      url: '/pages/person/mine'
    })
  }

})