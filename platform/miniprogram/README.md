# xcfp 小程序前端

原生微信小程序（WXML/WXSS/JavaScript，ES modules），对接 xcfp 的商品服务（8201）与用户服务（8202）。

## 运行

1. 用微信开发者工具导入本目录，AppID 可使用 `touristappid` 或自己的测试 AppID。
2. 启动 `../backend` 的三个服务和 MySQL（初始化步骤见仓库根 README）。
3. 开发者工具 → 详情 → 本地设置，勾选“不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书”。
4. 接口地址在 `utils/config.js`：

   ```js
   export default {
     productBase: 'http://127.0.0.1:8201',
     userBase: 'http://127.0.0.1:8202'
   }
   ```

   真机调试请改成电脑的局域网 IP（如 `http://192.168.1.10:8201`），并确保手机与电脑同网、防火墙放行端口；正式发布必须使用已备案的 HTTPS 域名并在小程序后台配置 request 合法域名。

5. 演示账号：`13900000000 / demo123`。

## 页面与流程

```text
pages/index/index                     首页：分类筛选、商品分页、下拉刷新
pages/proinfo/proinfo                 商品详情：库存/数量校验、去下单
pages/pay/pay                         确认订单：选择地址、创建订单、模拟支付
pages/login/login                     登录
pages/register/register               注册
pages/personal/personal               个人中心：资料、订单入口、地址/反馈/关于
pages/personal/address/address        地址列表：选择、设默认、编辑、删除
pages/personal/address/saveadd/saveadd 新增/编辑地址
pages/personal/order/order            订单列表：状态筛选、分页、支付/取消/确认
pages/personal/order/detail/detail    订单详情：状态、收货信息、时间线、操作
pages/personal/feedback/feedback      意见反馈：提交、我的反馈
pages/personal/about/about            关于
pages/info/info                       资讯列表
pages/info/detail/detail              资讯详情
pages/logs/logs                       开发者日志（保留）
```

主流程：登录/注册 → 首页浏览 → 商品详情 → 选择地址 → 创建订单 → 模拟支付 → 订单列表/详情 → 确认收货；也支持取消未支付订单。地址、反馈、资讯详情均从后端读取，不再使用静态数组。

## 请求与登录态

- `utils/request.js` 统一封装请求：自动附加 `Authorization: Bearer <token>`，检查业务码。
- 业务码非 `200` 时提示；`208/HTTP 401` 会清除本地登录态并跳转登录页；`209/HTTP 403` 提示无权限。
- `utils/auth.js` 管理登录态，storage key：
  - `xcfp_token`
  - `xcfp_user`
  - `xcfp_selected_address_id`（支付页选择地址的临时值）
- 密码不写入本地存储；`app.js` 启动时会清理旧版本遗留的明文/临时 storage 键。

## 状态与支付

订单状态与后端一致：`created` 待付款、`paid` 待发货、`shipped` 待收货、`completed` 已完成、`cancelled` 已取消。

支付页调用 `POST /app/order/{orderid}/pay`，当前为**模拟支付**，不会唤起微信支付，也不产生真实扣款。需要真实支付时，在后端替换 `PaymentGateway` 实现，并在小程序侧接入 `wx.requestPayment`。

## 已知限制

- 没有购物车，单商品直接下单；
- 不接入真实微信支付/退款；
- 不做图片上传，商品图片使用后端返回的 URL；
- 比赛版 `competition-miniprogram/` 与本目录相互独立，本目录只对接 `platform/backend`。

## 静态校验

```bash
# 仓库根目录
node scripts/validate-miniprogram.mjs platform/miniprogram
```

该脚本检查 `app.json` 页面注册、页面文件是否存在、本地导航目标是否已注册。当前版本已在微信开发者工具中实际运行，登录、浏览、地址、下单、模拟支付、订单主流程可用；后续修改后仍建议按主流程手动回归。
