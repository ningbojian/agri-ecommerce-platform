# 关键设计与面试高频考点

> 本文以本仓库的实际代码为基准，整理面试中可能被追问的设计点。
> 建议先看“项目介绍模板”，再按模块复习；每个模块都区分了“项目里的实现”“为什么这样做”“面试追问”和“已知边界”。
>
> 这是课程/竞赛项目，不是完整生产系统。回答时既要讲清楚实现，也要主动说出局限和改进方向，避免把模拟支付、固定服务地址等方案说成生产级方案。

## 1. 项目介绍模板

### 1.1 30 秒版

这是一个电商助农全栈项目：用户端是原生微信小程序，管理端是 Vue 3，后端按业务边界拆成商品、用户/订单、供应商三个 Spring Boot 服务，各自使用独立的 MySQL 库。主链路是登录、商品浏览、地址管理、下单、模拟支付、后台发货和确认收货；项目重点是 JWT 鉴权、服务端定价、原子扣库存、订单状态机和服务间补偿。

### 1.2 2 分钟版

可以在 30 秒版基础上补充：

1. **拆分方式**：商品服务负责商品、分类、资讯和库存；用户服务负责用户/管理员认证、地址、订单、模拟支付和反馈；供应商服务负责供应商资料与审核。三个库之间不建外键，跨服务通过 HTTP 调用。
2. **安全边界**：JWT 由用户服务统一签发，三个服务共享密钥验签；`/app/**` 和 `/admin/**` 按角色校验；用户资源从 token 取用户 ID 做归属校验；内部库存接口使用独立的 `X-Internal-Token`。
3. **下单一致性**：商品服务用一条带 `stock >= ?` 条件的原子 SQL 扣库存并返回商品快照；用户服务用快照计算金额、保存订单；订单落库失败时调用释放接口补偿。
4. **订单状态机**：`created → paid → shipped → completed`，`created → cancelled`；支付、取消、发货、确认收货都在服务端校验当前状态。
5. **支付边界**：定义 `PaymentGateway` 接口，默认 `MockPaymentGateway` 返回模拟成功并写支付流水；`WechatPaymentGateway` 只是占位，没有接入商户号。
6. **工程化**：提供 `reset/schema/seed` 数据库脚本、83 项后端冒烟检查、管理后台构建和页面回归、小程序页面/导航静态校验。

### 1.3 面试官常问的开放问题

| 问题 | 回答方向 |
|---|---|
| 你负责哪部分？ | 按实际情况回答，只说你真正做过的模块；可以对应到本仓库的认证、订单、库存、管理后台或小程序。 |
| 项目最难的点是什么？ | 优先讲“多服务下库存和订单的一致性”，再讲 JWT 权限边界。难点不只是写接口，而是失败场景和边界条件。 |
| 项目有什么不足？ | 主动说：没有分布式事务、JWT 不能主动失效、内部令牌是静态共享密钥、没有真实支付和上传、测试以冒烟为主。 |
| 为什么做这个项目？ | 把 2021 年比赛版的静态商品原型，续作成可运行的前后端闭环，同时练习服务拆分、鉴权和订单链路。 |

## 2. 整体架构与服务拆分

### 2.1 服务与职责

| 服务 | 端口 | 数据库 | 主要职责 |
|---|---|---|---|
| `service_proinfo` | 8201 | `xcfp_pro` | 商品、分类、资讯；库存预占/释放内部接口 |
| `service_user` | 8202 | `xcfp_user` | 用户/管理员认证、地址、订单、模拟支付、反馈 |
| `service_sup` | 8203 | `xcfp_sup` | 供应商资料、审核、启停 |

请求链路：

```text
小程序 ──> service_proinfo (8201)  商品/资讯/库存
   └────> service_user   (8202)  登录/地址/订单/支付/反馈
管理后台 ──> 三个服务（Vite 代理 /pro-api、/user-api、/sup-api）
service_user ──/internal/product/reserve|release──> service_proinfo
```

### 2.2 为什么这样拆

**项目里的做法**：按业务能力拆分，每个服务只访问自己的数据库；用户服务在下单时需要库存，因此通过商品服务的内部接口获取商品快照并扣库存。

**这样做的理由**：

- 商品、交易、供应商的生命周期和访问频率不同，拆开后边界比较清楚。
- 每个服务可以独立构建、启动和扩展；数据库按服务隔离，避免直接跨库写表。
- 这是一个适合练习“服务边界、接口契约、失败补偿”的规模，不会为了微服务而拆得过细。

**代价**：

- 跨服务查询和一致性变复杂，不能用本地事务覆盖两个库。
- 需要处理超时、重试、重复调用、补偿失败等问题。
- 本项目没有注册中心、网关、配置中心、消息队列，服务地址是配置项，属于轻量级服务化，不是完整微服务治理体系。

### 2.3 三个库为什么没有外键

**做法**：`sql/schema.sql` 中三个数据库分别建表，表之间不建外键；服务端在业务层校验关联关系，例如下单前校验地址归属、商品是否可售。

**原因**：

- 外键不能跨数据库实例/库生效，拆库后本来就需要应用层保证。
- 避免级联更新/删除和跨服务锁竞争，让各服务的数据边界更清晰。
- 订单保存商品和地址快照，本身就不依赖历史商品/地址记录继续存在。

**代价**：可能出现孤儿数据；需要应用层校验、定期核对和清理。唯一性约束仍然放在数据库，例如手机号、订单号、支付单号。

### 2.4 服务间调用

`ProductClient` 使用 `RestTemplate` 调用商品服务：

- 连接超时 3 秒、读取超时 5 秒，避免商品服务异常时长时间占住用户服务线程。
- 请求头携带 `X-Internal-Token`，商品服务的 `InternalTokenInterceptor` 校验后再放行 `/internal/**`。
- 调用失败时把商品服务的业务码透传出来，例如库存不足 `4201`、商品不可售 `4203`。
- 这是同步调用，没有重试、熔断、服务发现和链路追踪；生产环境需要补这些能力。

### 2.5 面试追问

**问：为什么不用一个单体应用？**

答：这个项目规模确实可以用单体，拆成三个服务主要是为了练习业务边界和服务间一致性。拆分不是目的；如果能重来，我会先把单体边界做清楚，再根据团队规模、部署和扩展需求决定是否拆。

**问：为什么不用 Spring Cloud？**

答：项目只有三个服务，引入注册中心、网关、配置中心会增加运行和调试成本。当前用固定 URL + 内部令牌足够支撑本地演示；生产环境再引入网关、服务发现、配置中心和可观测性。

**问：服务调用失败怎么办？**

答：读操作可以提示重试；写操作要区分是否已经产生副作用。本项目的库存预占成功后会尝试本地落库，落库异常时调用释放接口补偿。更严谨的方案是本地消息表/Outbox、TCC 或对账任务，不能只依赖一次同步补偿。

**问：三个服务共用一个 JWT 密钥有什么风险？**

答：任何一个服务泄露密钥都能伪造任意角色令牌。生产环境应改用非对称签名（例如 RS256），只有认证服务持有私钥，其他服务只拿公钥验签；同时做密钥轮换和令牌版本管理。

## 3. 认证与授权

### 3.1 JWT 的结构

JWT 由三部分组成：`header.payload.signature`。

- **header**：签名算法和类型，例如 HS256。
- **payload**：claims，例如 `sub`（用户/管理员 ID）、`role`、`identity`、`iat`、`exp`。
- **signature**：用密钥对前两部分签名，防止内容被篡改。

注意：payload 只是 Base64Url 编码，**不是加密**，不要放密码、手机号等敏感信息。本项目放了用户 ID、角色和 `identity`（用户手机号/管理员用户名），没有放密码；但手机号属于 PII，生产环境应只放 ID，或改用加密 token/不透明 token。

### 3.2 项目中的 JWT 流程

1. `service_user` 的 `AuthService` 校验账号密码。
2. `JwtUtil` 用 HS256 签发 token：用户默认 7 天（604800 秒），管理员默认 12 小时（43200 秒）。
3. 三服务共享 `XCFP_JWT_SECRET`，`AuthInterceptor` 在请求进入 Controller 前解析 token。
4. 解析成功后写入 `UserContext`（ThreadLocal），Controller 从 context 取用户 ID，不读取客户端传入的 `userid`。
5. 请求结束后 `afterCompletion` 清理 ThreadLocal，避免线程复用导致用户串号。

### 3.3 路径鉴权规则

| 路径 | 要求 | 白名单 |
|---|---|---|
| `/app/**` | USER 角色 | `/app/auth/**`、`/app/product/**`、`/app/article/**` |
| `/admin/**` | ADMIN 角色 | `/admin/auth/login` |
| `/internal/**` | `X-Internal-Token` | 不走 JWT，由内部令牌拦截器校验 |

拦截器的判断顺序：

1. `OPTIONS` 预检直接放行；
2. Swagger、错误页等固定路径放行；
3. 读取 `Authorization: Bearer <token>`；
4. 验签、解析 claims，写入 `UserContext`；
5. 根据 URI 前缀校验角色；
6. 失败时写统一 JSON：未登录 HTTP 401 + code 208，角色不符 HTTP 403 + code 209。

### 3.4 认证和授权的区别

- **认证（Authentication）**：你是谁。登录、JWT 验签属于认证。
- **授权（Authorization）**：你能做什么。角色校验、资源归属校验属于授权。

只做角色校验是不够的。例如用户 A 拿到用户 B 的地址 ID，如果接口只检查“已登录”，A 仍然可以读写 B 的地址，这是典型的 **IDOR（不安全的直接对象引用）**。

本项目的处理：

- 地址服务 `getOwned(id, userId)` 先查记录，再比较 `userid` 和 token 中的用户 ID；
- 订单服务 `getUserOrder` 同样比较订单归属；
- 更新地址、删除地址、设默认地址都先做归属校验；
- 管理端订单查询走 `/admin/**`，只允许 ADMIN 角色。

### 3.5 HTTP 状态码与业务码

项目同时使用 HTTP 状态码和业务码：

- 未登录/令牌无效：HTTP 401，业务码 208；
- 路径/角色不匹配：HTTP 403，业务码 209；
- 资源归属不符或业务越权：统一异常处理器返回响应体 code 209，HTTP 通常是 200；
- 库存不足、订单状态不允许等：响应体业务码 4201/4202/4203，HTTP 仍可能是 200。

面试时要能解释：HTTP 状态码表达传输/认证层语义，业务码表达业务语义；前端 Axios 拦截器两种都会检查。当前实现里“归属越权”没有把 HTTP 改成 403，这是一个可以改进的一致性问题。

### 3.6 BCrypt 密码存储

项目使用 `BCryptPasswordEncoder(10)`：

- BCrypt 自带随机 salt，salt 和 cost 都编码在哈希字符串里，数据库不需要单独存 salt。
- cost=10 表示计算成本，登录时会消耗一定 CPU，增加暴力破解成本。
- `PasswordUtil.matches` 用 BCrypt 校验；如果数据库里是旧明文密码，则按明文比较，登录成功后立即用 BCrypt 回写，实现平滑升级。
- 用户密码、管理员密码、供应商密码字段使用 `WRITE_ONLY`，序列化时不返回前端。

**为什么不直接 MD5/SHA？**

MD5/SHA 速度太快，适合校验文件完整性，不适合存密码；不加盐还会被彩虹表批量破解。BCrypt 通过加盐和可调工作因子，让每次猜测都有成本。

**面试追问：**

- “BCrypt 的 salt 存在哪里？”——在哈希字符串内部，`matches` 会解析出来。
- “为什么要升级旧明文密码？”——历史数据不能直接丢弃，登录成功后有一次拿到明文的机会，可以无感迁移。
- “密码能不能返回给前端？”——不能；接口只返回用户资料字段，密码字段只写不读。
- “BCrypt 能不能防内网拖库？”——不能完全防；它能降低离线破解速度，还需要数据库权限、审计、加密备份等措施。

### 3.7 JWT 的局限与改进

**当前局限**：

- JWT 无状态，服务端不能立即让某个 token 失效；用户“退出登录”只是前端删除 token，旧 token 在过期前仍然有效。
- 管理员被禁用后，已签发的 token 在过期前仍可能通过验签；登录时检查了状态，但拦截器没有每次查库。
- 三服务共享 HS256 密钥，密钥泄露影响所有服务。
- 管理后台把 token 存在 `localStorage`，存在 XSS 读取风险。

**改进方向**：

- 短期 access token + 长期 refresh token；
- Redis 黑名单或 token version，修改密码/禁用账号时递增版本；
- 生产使用 HttpOnly + Secure + SameSite Cookie，并加 CSRF 防护；
- 非对称签名，认证服务持有私钥；
- 所有环境强制 HTTPS。

### 3.8 面试追问

**问：JWT 和 Session 怎么选？**

答：JWT 适合多服务、无状态验签和移动端；Session 更容易主动失效和集中管理。这个项目选 JWT 是为了让三个服务共享登录态，不依赖统一的 Session 存储；代价是注销和踢人能力弱。

**问：JWT 被盗用了怎么办？**

答：立即轮换密钥或把 token 加入黑名单；更细粒度可以按用户维护 token version。根治要靠 HTTPS、XSS 防护、短过期时间、HttpOnly Cookie 和异常登录检测。

**问：Token 过期后前端怎么处理？**

答：管理后台 Axios 拦截器遇到 208/HTTP 401 会清空本地会话并跳转登录页；小程序请求封装收到 208 也会清登录态并跳登录。生产环境可以加无感刷新。

**问：为什么不用 `userid` 参数？**

答：客户端可以随意改参数，必须从服务端可信的 token 中取用户 ID，再做资源归属校验，避免越权。

## 4. 订单状态机与幂等

### 4.1 状态与迁移

| 当前状态 | 操作 | 目标状态 | 触发方 |
|---|---|---|---|
| `created` | 支付 | `paid` | 用户 |
| `created` | 取消 | `cancelled` | 用户 |
| `paid` | 发货 | `shipped` | 管理员 |
| `shipped` | 确认收货 | `completed` | 用户 |

不在表里的迁移一律拒绝，例如未支付不能发货、已取消不能支付、已完成不能取消。

### 4.2 为什么状态机放在服务端

前端按钮可以隐藏，但不能作为权限依据；用户可以直接构造请求。因此 `OrderServiceImpl` 在支付、取消、发货、确认收货时都会重新查订单并校验当前状态。前端只负责展示和减少误操作。

### 4.3 幂等的含义

幂等指同一个操作执行多次，结果和执行一次一致。

项目里的顺序幂等：

- 支付：订单已经是 `paid/shipped/completed` 时直接返回当前订单，不重复写支付流水；
- 取消：订单已经是 `cancelled` 时直接返回，不重复释放库存；
- 确认收货/发货：只有特定前置状态允许，重复调用会被状态校验拒绝。

### 4.4 当前实现的并发边界

这段要如实说明，面试官很可能会追问：

- `pay` 和 `cancel` 都是“先查状态、再更新”，没有加行锁或版本号。两个并发请求可能同时读到 `created`，然后都继续执行。
- `payment_record` 没有对 `orderid` 建唯一约束，极端情况下可能产生两条支付记录。
- `restoreStock` 没有幂等标记，如果释放接口被重复调用，库存可能被多加。
- 订单号由“时间 + 4 位随机数”生成，有唯一索引兜底，但高并发下需要重试或改用发号器。

**更严谨的改进**：

- 用条件更新：`UPDATE orderinfo SET state='paid' WHERE orderid=? AND state='created'`，根据影响行数判断是否成功；
- 给 `payment_record.orderid` 加唯一约束，或引入支付单号/幂等键；
- 取消时先用条件更新抢占状态，再释放库存；
- 释放接口增加幂等键或按订单状态判断，避免重复回补；
- 对订单号使用数据库序列、雪花算法或 Redis 发号器。

### 4.5 面试追问

**问：支付回调重复通知怎么办？**

答：真实支付必须以支付平台的回调为准，回调要做验签、金额核对和幂等处理；可以用支付单号/订单号做唯一约束，已处理过的回调直接返回成功。当前模拟支付没有回调。

**问：为什么订单要保存商品快照？**

答：商品名称、价格、图片可能变化，历史订单必须保持下单时的信息；快照也避免跨服务实时查询商品。

**问：订单超时未支付怎么处理？**

答：当前没有自动关单。生产环境可以用定时任务扫描 `created` 且超时的订单，条件更新为 `cancelled` 并释放库存；释放动作仍要幂等。

## 5. 库存与防止超卖

### 5.1 超卖是怎么产生的

典型错误写法：

```sql
SELECT stock FROM product_info WHERE id = ?;
-- 在应用层判断 stock >= num
UPDATE product_info SET stock = stock - ? WHERE id = ?;
```

两个请求可能同时读到 `stock=1`，都判断通过，最后都扣减，库存变成负数。这是“先查后扣”的竞态。

### 5.2 项目里的原子扣减

`ProductInfoMapper.deductStock` 用一条 SQL 完成判断和扣减：

```sql
UPDATE product_info
SET stock = stock - #{num}, sales = sales + #{num}
WHERE id = #{productId}
  AND stock >= #{num}
  AND publish_status = 1
  AND audit_status = 1
  AND is_delete = 0;
```

关键点：

- `stock >= #{num}` 放在 `WHERE` 里，由数据库在行锁下判断，避免应用层竞态；
- `publish_status/audit_status/is_delete` 同时校验，防止下架或未审核商品被下单；
- 返回受影响行数：0 表示没有扣成功，再查商品区分“库存不足”和“商品不可售”；
- `stock` 和 `sales` 在同一条 UPDATE 中更新，保持一致性。

### 5.3 下单流程

```text
用户服务：
1. 从 token 取 userId，校验地址归属
2. 调商品服务 /internal/product/reserve
3. 商品服务原子扣库存并返回商品快照
4. 用户服务用快照单价 × 数量计算总额
5. 写订单（包含商品/地址快照）
6. 如果落库异常，调用 /internal/product/release 补偿
```

订单金额由服务端计算，客户端只传 `productId/num/addressId`，不能传价格。数量还有 `@Min(1)`、`@Max(99)` 校验。

### 5.4 取消与补偿

- 只有 `created` 状态可以取消；
- 先把订单改为 `cancelled`，再调用释放库存；
- 释放失败会抛异常并回滚本地事务，订单仍保持待付款，用户可以重试；
- 顺序重复取消会直接返回当前订单，避免顺序场景下二次释放。

### 5.5 当前实现的失败场景

这部分是加分项，说明自己知道边界：

1. **落库失败补偿**：`save(order)` 抛异常时会调用 release；但如果是事务提交阶段失败，`save` 方法已经返回，catch 捕获不到，补偿可能不会执行。
2. **补偿失败**：release 调用失败只记录日志/抛异常，没有重试队列和对账任务，库存可能暂时不一致。
3. **重复释放**：release 没有幂等键；并发取消或重复调用可能多加库存。
4. **跨服务事务**：没有 XA/Seata/TCC；当前是“本地事务 + 补偿”，属于最终一致性思路，但实现还不完整。

### 5.6 更完整的方案

- **本地消息表/Outbox**：订单事务内写一条库存释放消息，异步可靠投递；
- **TCC**：Try 预占、Confirm 确认、Cancel 释放，业务侵入更大；
- **消息队列 + 重试**：释放失败进入重试队列，超过次数告警；
- **对账任务**：定期比对订单状态和库存流水，修复不一致；
- **幂等键**：预占/释放都带订单号，服务端去重；
- **条件更新**：订单状态迁移用 `WHERE state = ?`，确保并发下只有一个请求成功。

### 5.7 面试追问

**问：为什么不用 Redis 扣库存？**

答：Redis 适合高并发预扣，但还要解决持久化、缓存与数据库一致性、失败回补等问题。这个项目没有高并发场景，直接依赖数据库行锁和条件更新更简单、更可靠。QPS 上来后可以考虑 Redis + 异步落库，但要设计好补偿。

**问：MySQL 的 UPDATE 为什么能防超卖？**

答：InnoDB 对更新行加锁，`WHERE stock >= ?` 在锁内判断，只有一个事务能把库存扣到满足条件的值；其他事务受影响行数为 0。

**问：库存扣了但订单没创建成功怎么办？**

答：代码在 `save` 异常时调用 release 补偿；更严谨要覆盖提交阶段失败、补偿失败和重复释放，用 Outbox/重试/对账兜底。

**问：取消订单后库存怎么恢复？**

答：更新订单为 `cancelled` 后调用 release，把 `stock` 加回去并回退 `sales`；失败则回滚本地事务，让用户重试。

## 6. 支付设计与真实接入

### 6.1 抽象方式

```java
public interface PaymentGateway {
    PaymentResult pay(Orderinfo order);
    String provider();
}
```

- `MockPaymentGateway`：默认实现，配置 `xcfp.pay.provider=mock` 或未配置时启用，返回成功和模拟交易号。
- `WechatPaymentGateway`：`xcfp.pay.provider=wechat` 时启用，目前调用会抛“未接入微信支付”。
- `OrderServiceImpl` 依赖接口，不直接依赖具体实现，后续替换/新增支付宝等方式不需要改订单主流程。

### 6.2 模拟支付流程

1. 校验订单归属和 `created` 状态；
2. 调用 `PaymentGateway.pay`；
3. 支付成功后订单改为 `paid`，写 `pay_time`；
4. 插入 `payment_record`，记录支付单号、订单号、金额、渠道、状态和交易号；
5. 重复支付（顺序场景）直接返回当前订单。

### 6.3 真实微信支付需要补什么

这部分一定要能说清楚边界：

- 商户号、API 密钥/证书、AppID 配置；
- 统一下单，获取 `prepay_id`；
- 小程序调用 `wx.requestPayment`；
- 支付回调验签，核对订单号、金额、商户号；
- 回调幂等：同一个支付单号只处理一次；
- 主动查单和对账，处理“用户看到支付成功但回调丢失”；
- 退款、关闭订单、异常订单处理；
- 密钥和证书不能写进代码仓库，使用配置中心/密钥管理；
- 真实支付不要在本地数据库事务里长时间等待第三方接口：先创建支付单/支付意图，回调或主动查单成功后再更新订单状态。

### 6.4 面试追问

**问：为什么用接口而不是直接写 if/else？**

答：支付渠道会变化，接口把“下单/支付结果”抽象出来，订单服务不需要知道微信/支付宝细节；新增渠道只需新增实现并切换配置。这是策略模式/依赖倒置的应用。

**问：怎么防止重复支付？**

答：当前按订单状态做顺序幂等；高并发下要用条件更新或分布式锁确保只有一个请求把订单从 `created` 改为 `paid`，并给支付流水加唯一约束。真实支付还要以平台回调/查单结果为准，不能只信客户端返回值。

**问：支付金额能被前端篡改吗？**

答：不能。创建订单时客户端只传商品和数量，金额由服务端按商品快照计算；支付时也校验订单金额，不接收客户端传入的金额。

## 7. 数据建模与查询

### 7.1 订单快照

订单表保存了 `proname/picurl/supname/price` 和收货人/电话/地址，而不是只存商品 ID 和地址 ID。原因：

- 商品可能改名、改价、下架，历史订单必须保持下单时信息；
- 地址可能被修改或删除，订单仍要能发货；
- 跨服务查询商品会增加耦合和延迟。

这是有意的反范式设计，用冗余换历史可追溯性。

### 7.2 金额与数量

- 金额使用 `BigDecimal` / MySQL `DECIMAL(10,2)`，不用 `float/double`，避免二进制浮点误差；
- 计算 `price × num` 后用 `setScale(2, RoundingMode.HALF_UP)`；
- 数量在请求 DTO 上限制 1~99；
- 订单号/支付单号有数据库唯一索引，作为最后一道防重。

### 7.3 索引与唯一约束

`schema.sql` 中主要的约束：

- `user.tel`、`admin_user.username`、`supinfo.suptel` 唯一；
- `orderinfo.orderid` 唯一，另有 `userid`、`(userid, state)`、`create_time` 索引；
- `payment_record.pay_no` 唯一，`orderid`、`userid` 有普通索引；
- 商品按 `supid`、`category_id`、`(publish_status, audit_status)` 建索引。

面试时可以说明：唯一索引不仅用于查询，也用于兜底业务唯一性；索引要围绕实际查询条件设计，不是越多越好。

### 7.4 分页与逻辑删除

- 分页使用 MyBatis-Plus `Page`，页码至少为 1，每页最多 50 条，避免客户端一次拉取过多数据。
- `is_delete` 使用 MyBatis-Plus `@TableLogic`，查询自动过滤，删除变更新；`@JsonIgnore` 避免逻辑删除标记返回前端。
- 时间字段使用 `LocalDateTime`，数据库用 `DATETIME`。

### 7.5 面试追问

**问：为什么不用外键？**

答：跨库无法使用外键；拆库后由应用层校验关联关系，避免跨服务锁和级联操作。代价是可能出现孤儿数据，需要用校验和对账兜底。

**问：订单为什么要冗余商品信息？**

答：保证历史订单可追溯，避免商品改价/下架后订单信息变化；这是典型的快照设计，用空间换一致性。

**问：金额为什么不用 double？**

答：double 有二进制精度问题，0.1+0.2 不等于 0.3；金额用 BigDecimal(数据库 DECIMAL) 并明确舍入规则。

## 8. 前端鉴权与请求层

### 8.1 管理后台

- Vue Router 使用 hash 路由；`beforeEach` 检查本地 token，未登录跳 `/login`，已登录访问登录页跳 `/products`。
- 三个 Axios 实例分别对应 `/pro-api`、`/user-api`、`/sup-api`，请求拦截器统一加 `Authorization: Bearer <token>`。
- 响应拦截器：`code === 200` 直接返回 `data`；`208` 或 HTTP 401 清会话并跳登录；其他错误弹提示。
- Vite 开发/预览服务器配置代理，避免浏览器跨域；生产环境需要 Nginx/网关做同样转发。
- token 存在 `localStorage`，适合本地演示；生产建议 HttpOnly Cookie + CSRF，或内存 token + refresh cookie。

### 8.2 小程序

- `utils/request.js` 统一封装请求，自动附加 Bearer token，检查业务码。
- 208/HTTP 401 清登录态并跳登录；209/HTTP 403 提示无权限。
- token 和用户信息存在微信 storage（`xcfp_token`、`xcfp_user`），密码不写入 storage；启动时清理旧版本的临时键。
- 商品/资讯走 8201，用户/订单走 8202；真机调试要改成局域网 IP，正式发布需要 HTTPS 合法域名。

### 8.3 面试追问

**问：前端路由守卫能代替后端鉴权吗？**

答：不能。路由守卫只改善体验，用户可以绕过前端直接请求接口；权限必须在后端校验。

**问：为什么开发环境用代理而不是 CORS？**

答：代理让浏览器认为请求同源，不需要处理预检和 Cookie 策略；生产环境可以在网关统一处理。当前后端也配置了 CORS，但 `allowedOrigins("*")` 只适合本地演示，生产要收紧。

**问：token 放 localStorage 有什么问题？**

答：XSS 可以读取 localStorage 并盗用 token；HttpOnly Cookie 能降低读取风险，但要配合 CSRF 防护。没有绝对安全，关键是 HTTPS、XSS 防护、短过期和最小权限。

## 9. 测试与工程化

### 9.1 验证手段

- `scripts/smoke-test.mjs`：覆盖注册/登录、商品分类、地址、下单/支付/发货/收货/取消、反馈、供应商 CRUD/审核，以及 401/403/越权/内部令牌等权限检查。
- `scripts/validate-miniprogram.mjs`：检查 `app.json` 页面注册、页面文件、导航目标。
- `sql/reset.sql → schema.sql → seed.sql`：可重复重建演示数据。
- 管理后台：`npm run build` 和浏览器回归，验证登录、页面渲染、CRUD、发货和反馈回复。

### 9.2 面试怎么讲测试

不要只说“测试过了”。可以说：

- 冒烟脚本从用户视角覆盖主链路，重点检查金额、库存、状态和权限；
- 负向用例包括未登录、角色不匹配、用户 A 访问用户 B 资源、库存不足、重复取消等；
- 当前没有 JUnit 单元测试和 CI，这是不足；下一步会把核心服务（订单状态机、库存、鉴权）补成单元测试，并把冒烟脚本接入 CI。

## 10. 高频问题速查

| # | 问题 | 一句话回答 | 展开 |
|---|---|---|---|
| 1 | 为什么拆三个服务？ | 按商品、交易、供应商边界拆分，独立库、独立部署；规模小，没有引入注册中心。 | 第 2 节 |
| 2 | 三库怎么保证一致性？ | 不做跨库事务，用接口调用、快照和失败补偿实现最终一致性。 | 2.3 / 5.5 |
| 3 | JWT 由谁签发？ | 用户服务统一签发，三服务共享密钥验签。 | 3.2 |
| 4 | JWT 能主动失效吗？ | 当前不能，只能等过期；生产用黑名单/token version/短过期 + refresh。 | 3.7 |
| 5 | 密码为什么用 BCrypt？ | 加盐、慢哈希、可调 cost，抗彩虹表和暴力破解。 | 3.6 |
| 6 | 如何防越权？ | 路径角色校验 + 从 token 取用户 ID + 资源归属校验。 | 3.3 / 3.4 |
| 7 | 401 和 403 区别？ | 401 未认证，403 已认证但无权限；项目还配了业务码 208/209。 | 3.5 |
| 8 | 如何防超卖？ | 一条带 `stock >= ?` 条件的原子 UPDATE，按影响行数判断。 | 5.2 |
| 9 | 为什么不能先查再扣？ | 两个请求可能同时读到相同库存，产生竞态。 | 5.1 |
| 10 | 下单失败库存怎么办？ | 调用 internal release 补偿；更严谨用 Outbox/重试/对账。 | 5.4 / 5.6 |
| 11 | 取消订单会重复回补库存吗？ | 顺序重复取消有状态判断；并发/重复调用还需要幂等键。 | 4.4 / 5.5 |
| 12 | 如何保证支付幂等？ | 当前按状态做顺序幂等；高并发要条件更新 + 支付流水唯一约束。 | 4.3 / 4.4 |
| 13 | 为什么金额由后端算？ | 客户端不可信，价格必须用服务端商品快照计算。 | 5.3 |
| 14 | 为什么保存订单快照？ | 商品/地址会变化，历史订单要可追溯。 | 7.1 |
| 15 | 为什么不用外键？ | 跨库无法建；应用层校验，代价是孤儿数据风险。 | 2.3 |
| 16 | 服务调用超时怎么办？ | 设置连接/读取超时；失败时明确报错，写操作要考虑补偿。 | 2.4 |
| 17 | 真实微信支付怎么接？ | 统一下单、prepay_id、wx.requestPayment、回调验签/幂等/对账。 | 6.3 |
| 18 | 项目最大的不足？ | 无分布式事务、幂等不完整、JWT 不可撤销、无真实支付/上传/CI。 | 第 11 节 |

## 11. 项目不足与改进路线

面试中主动说不足，比被问出来更好。可以按优先级讲：

| 优先级 | 问题 | 改进 |
|---|---|---|
| 高 | `pay/cancel/release` 在并发下不是严格幂等 | 条件更新 + 幂等键 + 支付流水唯一约束 + 重试/对账 |
| 高 | 跨服务库存补偿只覆盖 `save` 异常 | Outbox/本地消息表、失败重试、定时对账 |
| 高 | JWT 不能主动失效，禁用账号后旧 token 仍有效 | 短 access token + refresh、Redis 黑名单/token version |
| 高 | 内部令牌是静态共享密钥 | 密钥轮换、网络隔离、mTLS 或 OAuth2 client credentials |
| 中 | 没有 JUnit/CI，测试以冒烟为主 | 为核心服务补单元测试，接入 CI |
| 中 | 无服务发现/熔断/链路追踪 | 网关、注册中心、Resilience4j、OpenTelemetry |
| 中 | 管理后台 token 在 localStorage，CORS 为 `*` | HttpOnly Cookie + CSRF，收紧 CORS |
| 中 | 订单号随机后缀可能冲突 | 数据库序列/雪花算法/Redis 发号器 |
| 低 | 无真实支付、图片上传、细粒度 RBAC | 按业务阶段接入，不要为了演示过度设计 |

## 12. 代码索引

| 主题 | 代码位置 |
|---|---|
| JWT 签发/验签 | [JwtUtil.java](../platform/backend/common/common_util/src/main/java/xcfp/common/auth/JwtUtil.java) |
| 鉴权拦截器 | [AuthInterceptor.java](../platform/backend/common/common_util/src/main/java/xcfp/common/auth/AuthInterceptor.java) |
| 用户上下文 | [UserContext.java](../platform/backend/common/common_util/src/main/java/xcfp/common/auth/UserContext.java) |
| BCrypt 工具 | [PasswordUtil.java](../platform/backend/common/common_util/src/main/java/xcfp/common/auth/PasswordUtil.java) |
| 拦截器与 CORS 注册 | [WebAuthConfig.java](../platform/backend/common/common_util/src/main/java/xcfp/common/config/WebAuthConfig.java) |
| 内部令牌鉴权 | [InternalTokenInterceptor.java](../platform/backend/common/common_util/src/main/java/xcfp/common/auth/InternalTokenInterceptor.java) |
| 下单/支付/取消/状态机 | [OrderServiceImpl.java](../platform/backend/service/service_user/src/main/java/xcfp/user/service/impl/OrderServiceImpl.java) |
| 商品服务调用 | [ProductClient.java](../platform/backend/service/service_user/src/main/java/xcfp/user/client/ProductClient.java) |
| 库存原子扣减 | [ProductInfoMapper.java](../platform/backend/service/service_proinfo/src/main/java/xcfp/proinfo/mapper/ProductInfoMapper.java) |
| 内部库存接口 | [ProductInternalController.java](../platform/backend/service/service_proinfo/src/main/java/xcfp/proinfo/controller/ProductInternalController.java) |
| 支付接口/模拟实现 | [PaymentGateway.java](../platform/backend/service/service_user/src/main/java/xcfp/user/payment/PaymentGateway.java)、[MockPaymentGateway.java](../platform/backend/service/service_user/src/main/java/xcfp/user/payment/MockPaymentGateway.java) |
| 微信支付占位 | [WechatPaymentGateway.java](../platform/backend/service/service_user/src/main/java/xcfp/user/payment/WechatPaymentGateway.java) |
| 地址归属校验 | [UserAddressServiceImpl.java](../platform/backend/service/service_user/src/main/java/xcfp/user/service/impl/UserAddressServiceImpl.java) |
| 后台请求拦截 | [http.js](../platform/admin-web/src/api/http.js) |
| 后台路由守卫 | [router/index.js](../platform/admin-web/src/router/index.js) |
| 小程序请求封装 | [request.js](../platform/miniprogram/utils/request.js) |
| 小程序登录态 | [auth.js](../platform/miniprogram/utils/auth.js) |
| 数据库 schema | [schema.sql](../sql/schema.sql) |
| 后端冒烟测试 | [smoke-test.mjs](../scripts/smoke-test.mjs) |

## 13. 面试表达提醒

1. **先说结论，再说实现，最后说边界。** 例如“防超卖用条件更新，因为它把判断和扣减放在同一条 SQL 里；但它只解决单库库存，跨服务一致性还要补偿。”
2. **不要夸大**：模拟支付不是微信支付；没有注册中心就不要说“微服务治理完善”；没有分布式事务就说“最终一致性 + 补偿”。
3. **能指出自己的代码问题**：例如并发幂等、提交阶段补偿、内部令牌轮换，这说明你真的理解系统，而不是只背概念。
4. **把项目讲成取舍**：拆服务 vs 单体、JWT vs Session、数据库扣库存 vs Redis 扣库存，都有适用场景，没有绝对正确答案。
