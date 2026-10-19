const DB = wx.cloud.database().collection("pyq")
Page({
 data: {
   array: [],
    
 },
 onLoad() {
   let that = this
   DB.get({
     success(res) {
       that.setData({
         array: res.data
       })
       for (let i = 0; i < res.data.length; i++) {
         console.log(res.data[i].descption)
         console.log(res.data[i].imgURL)
       }
     }
   })
 },
 test:function(){
  wx.navigateTo({			//以navigate进行跳转
    url: '/pages/firstpage/info/share/share',			//保证路径正确，如果这样书写记得最前面加/，也可以这样写：../demo/demo
  })
},


})
