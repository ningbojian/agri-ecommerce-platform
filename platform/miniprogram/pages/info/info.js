// pages/info/info.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    myarray:[],
    swiper:[  
      {url:'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcfcoD0V4tBveXy9KVlCf1WL2IbI8BH9yCldr5z2Zb8jfO8B7vmdrTnCKzyHcNy7h0FwSCaINNpZIPj8QXwsted8!/b&bo=AAT8AQAAAAABF8k!&rf=viewer_4'} ,  
      {url:'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcfcoD0V4tBveXy9KVlCf1WKja6VRnzFQ8EktUtA7IUfSmPaa5m90zCOuVD9eL2KwzvMmJDSRgX*Rzte3ty*C8lE!/b&bo=gAKqAQAAAAABFxk!&rf=viewer_4'} ,  
      {url:'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcfcoD0V4tBveXy9KVlCf1WJBVz6Kvky1WwXPP4RzN4IPey33p0.zfz1qdVQxbR*cx4A.L7nR59pRTuuKHtMrdvU!/b&bo=qAKvAQAAAAABFzQ!&rf=viewer_4'}
      ],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var myarray1 = [{
      picture_url: "http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcY15ThT7isfaFDd0iCmPLN8LhcR.p.fUeGWYzNL2nfN5yUvxGRQj5CgWgO9HPweKP5gOc3pZ8Xnn1Ww.Kk5WeMY!/b&bo=cwH4AAAAAAABF7o!&rf=viewer_4",
      
      describe: "支持电商扶贫服务体系建设",
      brief_introduction: "——动员有志于扶贫事业的电商企业，搭建贫困地区产品销售网络平台和电商服务平台。支持银行业金融机构和非银行支付机构研发满足贫困地区电子商务发展需求的网上支付、手机支付等产品，加快贫困村村级电商服务点、助农取款服务点建设。",
  

    },
    {
      picture_url: "http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcUDYcu7Yia6SRyOZAaWypbMW3YeLQktF1yAZ0jvdlhYCUWRJ3uthqP1BzNH*EtpmM77ymfAIh5g3iSLRRr7KRs4!/b&bo=RgIsAQAAAAABF1k!&rf=viewer_4",
      
      describe: "鼓励贫困县成立电商扶贫",
      brief_introduction: "——鼓励贫困县成立电商扶贫协会等社会组织，为农村群众特别是贫困户提供产品集货、分级包装、品牌营销、物流配送、售后保障等服务，提高应对市场的能力；完善中国邮政县乡仓储中心布局；鼓励支持跨境电商发展。",
    },
    {
      picture_url: "http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcY15ThT7isfaFDd0iCmPLN81CZ4.Ni7YTX4.TA*IXFj2vnnZBkhWrRVClsgMyKb.NuKmy.uFoKvBQ*yZbl3MTvc!/b&bo=2gFDAQAAAAABF6k!&rf=viewer_4",
     
      describe: "多元平台、突出特色。",
      brief_introduction: "——选择国内较为成熟的第三方电商服务平台开展合作，结合不同电商企业发展方向和贫困地区实际情况，注重农副产品上行，突出特色、因地制宜，搭建贫困地区产品和电商平台间的桥梁。",

    },
    {
      picture_url: "http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mccdosSctT7DvP1*bTU*67I0srzmCOv5eltK1SupPSaQwnKStwnnf8182HO7xqzaT1kpNw8QY9JmnKUYVFcgP2Mk!/b&bo=gAKrAQAAAAABFxg!&rf=viewer_4",
     
      describe: "砀山酥梨滞销",
      brief_introduction: "——疫情导致酥梨滞销，影响面积非常之广。销路难以打开，一年辛苦劳作也成为无用功。为了帮助当地农民尽快解决滞销困境，发此公告。",

    },
    {
      picture_url: "http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcUDYcu7Yia6SRyOZAaWypbOewAy*JdH3MSeip7innR9z2zlBZ4gYEvau3TXpfmY4VViB5XSGmFQPd9hHsXB3Pkg!/b&bo=6AOQAgAAAAADRxs!&rf=viewer_4",
      
      describe: "顺应电商助农趋势",
      brief_introduction: "——让电商直播充分发挥其在助农中的积极作用，需要在顺应网络化、数字化、智能化趋势的前提下，切实发挥监管作用，保护好“直播助农”的良好生态。如此才能更好发挥其促进经济转型升级的重要作用，让“电商助农”为决战决胜脱贫攻坚贡献充足“网动力”。",

    }
  ]
  this.setData({
    myarray: myarray1
  });


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