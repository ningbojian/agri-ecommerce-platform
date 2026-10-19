# SQL 说明

`xcfp` 续作使用了三个 MySQL 数据库：

- `xcfp_pro`：商品（`product_info`）
- `xcfp_sup`：供应商（`supinfo`）
- `xcfp_user`：用户、地址、订单（`user`、`user_address`、`orderinfo`）

原始课程项目没有保留完整数据库 dump；本目录的 `schema.sql` 是根据 MyBatis-Plus 实体和旧 `product_info` DDL 片段重建的**最小可用 schema**，字段/索引可能与 2021 年原库略有差异。

```bash
mysql -u root -p < schema.sql
mysql -u root -p < seed.sql
```

`seed.sql` 只包含虚构演示数据（电话 `138/13900000000`、密码 `demo123`），不要用于真实业务。支付没有接入微信支付，`pay` 页只是写入订单。
