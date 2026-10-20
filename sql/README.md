# SQL 说明

xcfp 平台使用三个 MySQL 8 数据库，表之间不建外键：

| 数据库 | 表 | 说明 |
|---|---|---|
| `xcfp_pro` | `product_category` | 商品分类 |
| | `product_info` | 商品：价格、库存、销量、上下架、审核状态 |
| | `article` | 资讯 |
| `xcfp_user` | `user` | 小程序用户，密码 BCrypt |
| | `admin_user` | 管理后台账号，密码 BCrypt |
| | `user_address` | 收货地址，支持默认地址 |
| | `orderinfo` | 订单与收货/商品快照、状态机、物流、各阶段时间 |
| | `payment_record` | 支付流水，`channel=MOCK`、预留 `transaction_id` |
| | `feedback` | 用户反馈与管理员回复 |
| `xcfp_sup` | `supinfo` | 供应商资料、审核/启停状态，密码与密钥不返回前端 |

## 初始化

在仓库根目录执行：

```bash
mysql --default-character-set=utf8mb4 -u root -p < sql/reset.sql     # 可选：删除三库中的旧表，便于反复重建
mysql --default-character-set=utf8mb4 -u root -p < sql/schema.sql
mysql --default-character-set=utf8mb4 -u root -p < sql/seed.sql
```

Windows 下建议保留 `--default-character-set=utf8mb4`；否则部分客户端会用本地字符集发送文件，导致 `unit VARCHAR(16) DEFAULT '件'` 这类中文默认值报 `Invalid default value`。

- `schema.sql` 可重复执行（`CREATE DATABASE/TABLE IF NOT EXISTS`）。
- `seed.sql` 使用固定 ID + `ON DUPLICATE KEY UPDATE`，可重复执行，但会覆盖同 ID 的演示数据。
- `reset.sql` 会**删除数据**，仅用于本地演示环境；执行顺序必须是 reset → schema → seed。
- 字符集统一 `utf8mb4`。

## 演示数据

- 用户：`13900000000 / demo123`
- 管理员：`admin / admin123`
- 供应商：`演示助农供应商`（不提供登录入口）
- 8 个分类、8 个商品、5 篇资讯、5 条覆盖全部状态的订单、2 条支付流水、1 条已回复反馈

密码在 `seed.sql` 中以 BCrypt 哈希保存。以上账号、电话、证件号均为虚构演示数据。

## 与旧版差异

v2 schema 由实体重建，不是 2021 年原始库的完整 dump：

- 商品新增 `category_id/stock/sales/unit`，价格改为 `DECIMAL(10,2)`；
- 用户/供应商密码列扩到 100，存 BCrypt；
- 订单新增商品/地址快照与状态机时间字段；
- 新增分类、资讯、管理员、支付流水、反馈表；
- 旧库升级请先备份，再按 reset → schema → seed 重建。
