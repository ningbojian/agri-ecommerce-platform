-- xcfp 平台数据库 v2（全新环境）。
-- 已有旧库请先执行 reset.sql，再执行本文件，最后执行 seed.sql。
-- 三个服务各自使用一个数据库，表之间不建外键（跨库）。

CREATE DATABASE IF NOT EXISTS xcfp_pro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS xcfp_sup CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS xcfp_user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- xcfp_pro：分类、商品、资讯
-- ---------------------------------------------------------------------------
USE xcfp_pro;

CREATE TABLE IF NOT EXISTS product_category (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL,
  icon VARCHAR(512) DEFAULT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 启用 0 停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_category_name (name),
  KEY idx_category_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS product_info (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  proname VARCHAR(128) NOT NULL,
  supid BIGINT UNSIGNED NOT NULL DEFAULT 0,
  supname VARCHAR(64) DEFAULT NULL,
  category_id BIGINT UNSIGNED NOT NULL DEFAULT 0,
  delivery VARCHAR(64) DEFAULT NULL,
  price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  stock INT NOT NULL DEFAULT 0,
  sales INT NOT NULL DEFAULT 0,
  unit VARCHAR(16) NOT NULL DEFAULT '件',
  publish_status TINYINT NOT NULL DEFAULT 0 COMMENT '1 上架 0 下架',
  audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '0 待审核 1 通过 2 驳回',
  descript TEXT,
  picurl VARCHAR(512) DEFAULT NULL,
  sort VARCHAR(32) DEFAULT NULL COMMENT '旧版分类字段，仅兼容保留',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_product_supid (supid),
  KEY idx_product_category (category_id),
  KEY idx_product_status (publish_status, audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS article (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  title VARCHAR(128) NOT NULL,
  summary VARCHAR(255) DEFAULT NULL,
  content TEXT,
  cover_url VARCHAR(512) DEFAULT NULL,
  author VARCHAR(64) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 已发布 0 草稿',
  publish_time DATETIME DEFAULT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_article_status (status, publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------------
-- xcfp_sup：供应商
-- ---------------------------------------------------------------------------
USE xcfp_sup;

CREATE TABLE IF NOT EXISTS supinfo (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  suptel VARCHAR(20) NOT NULL,
  suppass VARCHAR(100) DEFAULT NULL COMMENT 'BCrypt；本期无供应商登录',
  secode VARCHAR(64) DEFAULT NULL COMMENT '历史字段，不再返回前端',
  supname VARCHAR(64) NOT NULL,
  cardnum VARCHAR(64) DEFAULT NULL,
  signkey VARCHAR(128) DEFAULT NULL COMMENT '服务端生成的签名密钥，不返回前端',
  logo VARCHAR(512) DEFAULT NULL,
  descript VARCHAR(500) DEFAULT NULL,
  audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '0 待审核 1 通过 2 驳回',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 正常 0 停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sup_tel (suptel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------------
-- xcfp_user：用户、管理员、地址、订单、支付流水、反馈
-- ---------------------------------------------------------------------------
USE xcfp_user;

CREATE TABLE IF NOT EXISTS user (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  tel VARCHAR(20) NOT NULL,
  password VARCHAR(100) NOT NULL COMMENT 'BCrypt',
  nickname VARCHAR(64) DEFAULT NULL,
  avatar VARCHAR(512) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 正常 0 禁用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_tel (tel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS admin_user (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(32) NOT NULL,
  password VARCHAR(100) NOT NULL COMMENT 'BCrypt',
  nickname VARCHAR(64) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 正常 0 禁用',
  last_login_time DATETIME DEFAULT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_admin_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_address (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  userid BIGINT UNSIGNED NOT NULL,
  address VARCHAR(255) NOT NULL,
  addtel VARCHAR(20) NOT NULL,
  addname VARCHAR(64) NOT NULL,
  is_default TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_address_userid (userid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS orderinfo (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  userid BIGINT UNSIGNED NOT NULL,
  supid BIGINT UNSIGNED DEFAULT NULL,
  orderid VARCHAR(64) NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL DEFAULT 0,
  proname VARCHAR(128) NOT NULL,
  picurl VARCHAR(512) DEFAULT NULL,
  supname VARCHAR(64) DEFAULT NULL,
  price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '下单时单价',
  pay DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '订单总额，服务端计算',
  num INT NOT NULL DEFAULT 1,
  addid BIGINT UNSIGNED DEFAULT NULL,
  receiver_name VARCHAR(64) DEFAULT NULL,
  receiver_tel VARCHAR(20) DEFAULT NULL,
  receiver_address VARCHAR(255) DEFAULT NULL,
  state VARCHAR(32) NOT NULL DEFAULT 'created' COMMENT 'created/paid/shipped/completed/cancelled',
  express VARCHAR(128) DEFAULT NULL,
  pay_time DATETIME DEFAULT NULL,
  ship_time DATETIME DEFAULT NULL,
  complete_time DATETIME DEFAULT NULL,
  cancel_time DATETIME DEFAULT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_order_orderid (orderid),
  KEY idx_order_userid (userid),
  KEY idx_order_user_state (userid, state),
  KEY idx_order_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS payment_record (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  pay_no VARCHAR(64) NOT NULL,
  orderid VARCHAR(64) NOT NULL,
  userid BIGINT UNSIGNED NOT NULL,
  amount DECIMAL(12, 2) NOT NULL,
  channel VARCHAR(16) NOT NULL COMMENT 'MOCK / WECHAT',
  status VARCHAR(16) NOT NULL COMMENT 'SUCCESS / FAILED / PENDING',
  transaction_id VARCHAR(64) DEFAULT NULL COMMENT '预留真实微信支付单号',
  pay_time DATETIME DEFAULT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_payment_payno (pay_no),
  KEY idx_payment_orderid (orderid),
  KEY idx_payment_user (userid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS feedback (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  userid BIGINT UNSIGNED NOT NULL,
  content VARCHAR(500) NOT NULL,
  contact VARCHAR(64) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0 待处理 1 已回复',
  reply VARCHAR(500) DEFAULT NULL,
  reply_time DATETIME DEFAULT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_feedback_userid (userid),
  KEY idx_feedback_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
