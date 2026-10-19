# xcfp Spring Boot 后端

2021.10-11 的电商助农续作，多模块 Spring Boot + MyBatis-Plus + MySQL。

## 模块

```text
backend/
├─ common/common_util        # 通用工具
├─ common/service_util       # Swagger/通用配置
├─ model/                    # ProductInfo/Supinfo/User/UserAddress/Orderinfo/VO
└─ service/
   ├─ service_proinfo        # 商品服务，8201
   ├─ service_user           # 用户/地址/订单服务，8202
   └─ service_sup            # 供应商服务，8203
```

## 本地运行

要求 JDK 8（本项目 `java.version=1.8`；本机 Maven 使用 1.8 运行时）和 Maven。

```bash
# 1. 建库和演示数据
mysql -u root -p < ../../sql/schema.sql
mysql -u root -p < ../../sql/seed.sql

# 2. 配置数据库环境变量（不要把真实密码提交到仓库）
export XCFP_DB_USER=root
export XCFP_DB_PASSWORD=change-me
export XCFP_PRO_DB_URL='jdbc:mysql://localhost:3306/xcfp_pro?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai'
export XCFP_SUP_DB_URL='jdbc:mysql://localhost:3306/xcfp_sup?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai'
export XCFP_USER_DB_URL='jdbc:mysql://localhost:3306/xcfp_user?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai'

# 3. 构建（已验证可通过）
mvn -B -DskipTests clean package

# 4. 分别启动
java -jar service/service_proinfo/target/service_proinfo-1.0.0.jar
java -jar service/service_user/target/service_user-1.0.0.jar
java -jar service/service_sup/target/service_sup-1.0.0.jar
```

Swagger：`http://localhost:8201/swagger-ui.html` 等（按服务端口）。

## 数据库与兼容性

- `../../sql/schema.sql` 是根据实体和旧 DDL 片段重建的最小 schema，不是原始完整 dump。
- 项目使用 `mysql-connector-java 5.1.46`；MySQL 8 可能需要 `mysql_native_password` 演示账号或升级 connector 到 8.x，并在 README 记录你的选择。
- 支付未接入真实渠道；订单只是写入数据库。

## 已知未完成项

- 没有原始完整数据库 dump/测试；
- 服务间调用、登录权限、支付和部署配置是课程原型级别；
- 不要使用仓库里的演示电话/密码作为真实业务数据。
