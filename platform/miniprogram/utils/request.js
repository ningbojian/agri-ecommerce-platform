// 发送ajax请求
import config from './config'
export default (url,data={},method='POST')=>{
 return new Promise((resoive,reject)=>{
    //  new promise初始化promise实例的状态为pedding
    wx.request({
    url:config.host+url,
    data,
    method,
    success:(res)=>{
    //   console.log('成功',res);
    // 修改promise为成功状态
      resoive(res.data);

    },
    fail:(err)=>{
    //   console.log('失败',err);
      reject(err);
    }
  })
})
}