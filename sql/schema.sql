-- Reconstructed minimum schema for the 2021 xcfp continuation.
-- The original course project did not include a complete database dump.
-- Run this file with MySQL 8 (or 5.7) before starting the three services.

CREATE DATABASE IF NOT EXISTS xcfp_pro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS xcfp_sup CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS xcfp_user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE xcfp_pro;

CREATE TABLE IF NOT EXISTS product_info (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  proname VARCHAR(128) NOT NULL,
  supid BIGINT UNSIGNED NOT NULL,
  supname VARCHAR(64) NOT NULL,
  delivery VARCHAR(64) DEFAULT NULL,
  price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  publish_status TINYINT NOT NULL DEFAULT 0,
  audit_status TINYINT NOT NULL DEFAULT 0,
  descript VARCHAR(255) DEFAULT NULL,
  picurl VARCHAR(255) DEFAULT NULL,
  sort VARCHAR(32) DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_product_supid (supid),
  KEY idx_product_status (publish_status, audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE xcfp_sup;

CREATE TABLE IF NOT EXISTS supinfo (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  suptel VARCHAR(20) NOT NULL,
  suppass VARCHAR(64) NOT NULL,
  secode VARCHAR(64) DEFAULT NULL,
  supname VARCHAR(64) NOT NULL,
  cardnum VARCHAR(64) DEFAULT NULL,
  signkey VARCHAR(128) DEFAULT NULL,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sup_tel (suptel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE xcfp_user;

CREATE TABLE IF NOT EXISTS user (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  tel VARCHAR(20) NOT NULL,
  password VARCHAR(128) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_tel (tel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_address (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  userid BIGINT UNSIGNED NOT NULL,
  address VARCHAR(255) NOT NULL,
  addtel VARCHAR(20) NOT NULL,
  addname VARCHAR(64) NOT NULL,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_address_userid (userid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS orderinfo (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  userid BIGINT UNSIGNED NOT NULL,
  supid BIGINT UNSIGNED NOT NULL,
  orderid VARCHAR(64) NOT NULL,
  proname VARCHAR(128) NOT NULL,
  supname VARCHAR(64) NOT NULL,
  price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  pay DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  num BIGINT NOT NULL DEFAULT 1,
  addid BIGINT UNSIGNED DEFAULT NULL,
  state VARCHAR(32) DEFAULT 'created',
  express VARCHAR(128) DEFAULT NULL,
  is_delete TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_order_orderid (orderid),
  KEY idx_order_userid (userid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
