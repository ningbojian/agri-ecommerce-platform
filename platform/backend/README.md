# xcfp Spring Boot 后端

电商助农平台的三服务后端：Spring Boot 2.2 + MyBatis-Plus + MySQL 8，JDK 8。

## 模块

```text
backend/
├─ common/common_util        # Result、异常处理、JWT、鉴权拦截器、BCrypt、内部令牌
├─ common/service_util       # Swagger 与通用配置
├─ model/                    # 实体、分页结果、VO/DTO
└─ service/
   ├─ service_proinfo        # 商品/分类/资讯，8201，库 xcfp_pro
   ├─ service_user           # 认证/用户/地址/订单/支付/反馈，8202，库 xcfp_user
   └─ service_sup            # 供应商管理，8203，库 xcfp_sup
```

## 本地运行

要求 JDK 8（`java.version=1.8`）和 Maven 3.6+。

```bash
# 1. 初始化数据库（在仓库根目录执行）
mysql --default-character-set=utf8mb4 -u root -p < ../../sql/reset.sql
mysql --default-character-set=utf8mb4 -u root -p < ../../sql/schema.sql
mysql --default-character-set=utf8mb4 -u root -p < ../../sql/seed.sql

# 2. 配置环境变量（不要把真实密码提交到仓库；本机 root 无密码时设为 ''）
export XCFP_DB_USER=root
export XCFP_DB_PASSWORD=change-me
# 可选：覆盖默认 127.0.0.1 连接串
# export XCFP_PRO_DB_URL='jdbc:mysql://127.0.0.1:3306/xcfp_pro?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true'
# export XCFP_SUP_DB_URL='...xcfp_sup...'
# export XCFP_USER_DB_URL='...xcfp_user...'
export XCFP_JWT_SECRET='xcfp-local-dev-secret-please-change-0123456789'
export XCFP_INTERNAL_TOKEN='xcfp-local-internal-token'
export XCFP_PRODUCT_SERVICE_URL='http://127.0.0.1:8201'

# 3. 构建
mvn -B -DskipTests clean package

# 4. 分别启动（三个终端）
java -jar service/service_proinfo/target/service_proinfo-1.0.0.jar
java -jar service/service_user/target/service_user-1.0.0.jar
java -jar service/service_sup/target/service_sup-1.0.0.jar
```

本机 PATH 中的 `java` 不是 JDK 8 时，用完整的 `%JAVA_HOME%\bin\java -jar` 启动；`mvn -v` 应显示 Java 1.8。

Swagger（默认开启）：

- `http://127.0.0.1:8201/swagger-ui.html`
- `http://127.0.0.1:8202/swagger-ui.html`
- `http://127.0.0.1:8203/swagger-ui.html`

若 Springfox 在特定 JDK/Spring Boot 组合下启动失败，可设置 `XCFP_SWAGGER_ENABLED=false`，不影响业务接口。

## 鉴权与权限

- JWT 由 `service_user` 签发，三服务共享 `XCFP_JWT_SECRET` 本地验签。
- 载荷：`sub`（用户/管理员 ID）、`role`（`USER` / `ADMIN`）、`identity`、过期时间。
- 用户令牌默认 7 天，管理员令牌默认 12 小时，可通过 `xcfp.jwt.*-expire-seconds` 调整。
- 路径规则：
  - `/app/**`：USER 令牌；白名单为 `/app/auth/**`、`/app/product/**`、`/app/article/**`
  - `/admin/**`：ADMIN 令牌；白名单为 `/admin/auth/login`
  - `/internal/**`：`X-Internal-Token` 共享令牌，供服务间调用
- 未登录返回业务码 `208` + HTTP 401；越权返回 `209` + HTTP 403。
- 用户地址、订单等接口从 token 取用户 ID 并校验归属，不信任请求体中的 `userid`。
- 敏感字段：用户密码、管理员密码、供应商密码使用 `WRITE_ONLY`；`signkey/secode`、逻辑删除标记不序列化。

## 主要接口

| 服务 | 方法/路径 | 说明 |
|---|---|---|
| user | `POST /app/auth/register`、`POST /app/auth/login` | 小程序注册/登录 |
| user | `GET/PUT /app/user/profile`、`PUT /app/user/password` | 个人资料、改密 |
| user | `GET/POST/DELETE /app/address/**` | 地址列表、默认地址、增删改 |
| user | `POST /app/order/create`、`GET /app/order/page`、`GET /app/order/{orderid}` | 下单、列表、详情 |
| user | `POST /app/order/{orderid}/pay|cancel|confirm` | 模拟支付、取消、确认收货 |
| user | `POST /app/feedback`、`GET /app/feedback/my` | 反馈 |
| user | `POST /admin/auth/login` | 管理员登录 |
| user | `GET /admin/order/page`、`GET /admin/order/{orderid}`、`POST /admin/order/{orderid}/ship` | 管理端订单/发货 |
| user | `GET /admin/user/page`、`GET /admin/feedback/page`、`POST /admin/feedback/{id}/reply` | 用户/反馈管理 |
| pro | `GET /app/product/categories|page|{id}`、`GET /app/article/page|{id}` | 公开浏览 |
| pro | `/admin/product/**`、`/admin/category/**`、`/admin/article/**` | 管理端 CRUD、上下架、库存、审核 |
| pro | `POST /internal/product/reserve|release` | 内部库存预占/释放 |
| sup | `/admin/sup/**` | 供应商分页、CRUD、审核、启停 |

## 订单与库存

- 状态机：`created`（待付款）→ `paid`（待发货）→ `shipped`（待收货）→ `completed`（已完成）；`created` 可取消为 `cancelled`。
- 创建订单：校验地址归属 → 调商品服务原子扣减库存并返回商品快照 → 服务端计算总额 → 写订单；落库失败会调用释放接口补偿。
- 取消订单：仅 `created` 允许；更新状态后释放库存，重复取消不会二次恢复。
- 支付为模拟闭环：`PaymentGateway` 接口 + `MockPaymentGateway` 默认实现，写 `payment_record`；`WechatPaymentGateway` 仅为占位，未接入真实微信支付。

## 数据库与兼容性

- MySQL 8，驱动 `mysql-connector-java 8.0.33`，URL 使用 `com.mysql.cj.jdbc.Driver`。
- 默认连接 `127.0.0.1`（避免 Windows IPv6 解析问题），并带 `allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Shanghai`。
- `sql/schema.sql` 是按实体重建的可运行 schema；`sql/seed.sql` 为虚构演示数据。
- 旧库升级请先备份，再执行 `sql/reset.sql`、`sql/schema.sql`、`sql/seed.sql`。

## 验证

```bash
# 仓库根目录；需三服务已启动
node scripts/smoke-test.mjs
```

该脚本覆盖注册/登录、分类商品、地址、下单与库存、模拟支付、管理端登录/发货、确认收货、取消回补库存、反馈回复、供应商 CRUD/审核，以及 401/403/越权/内部令牌等权限检查。测试会写入演示数据，结束后可用 `sql/reset.sql` 恢复。

## 已知限制

- 无真实支付/退款、无图片上传、无 Docker/CI；
- 未做细粒度 RBAC，仅 USER/ADMIN 两类角色；
- 服务间调用为同步 HTTP，未引入消息队列与分布式事务；
- 仓库演示电话/密码均为虚构值，不要用于真实业务。
