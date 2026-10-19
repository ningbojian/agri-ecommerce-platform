# xcfp 小程序前端

原生微信小程序，连接 xcfp 的 Spring Boot 后端（商品/用户/地址/订单）。

## 运行

1. 用微信开发者工具导入本目录，AppID 使用 `touristappid`。
2. 启动 `../backend` 的三个服务（8201/8202/8203）。
3. `utils/config.js` 默认指向 `http://localhost:8201` / `http://localhost:8202`；本地开发时在 DevTools 关闭域名校验，真机/线上需要 HTTPS 域名。
4. 先在 MySQL 执行 `sql/schema.sql` 和 `sql/seed.sql`，演示账号见 `sql/seed.sql`（`13900000000` / `demo123`）。

## 页面

首页、商品详情、信息发布、个人中心、地址管理、订单、支付、登录、注册、关于。

支付页只提交订单，**没有接入真实微信支付**。后端接口/数据库结构见 `../backend/README.md` 和 `../../sql/README.md`。

## 已知未完成项

- 远程商品图片仍是 2021 年的外链，部分可能失效；
- 登录/注册/支付需要配合本地后端与 MySQL 才能完整走通；
- 没有购物车；支付是下单占位；
- 2021 年原始数据/接口没有完整保留，schema 为重建版本。
