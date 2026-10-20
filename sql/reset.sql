-- 清空 xcfp 三个库中的表（保留数据库本身），用于本地反复重建演示数据。
-- 执行顺序：reset.sql -> schema.sql -> seed.sql

CREATE DATABASE IF NOT EXISTS xcfp_pro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS xcfp_sup CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS xcfp_user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE xcfp_pro;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS product_info;
DROP TABLE IF EXISTS product_category;

USE xcfp_sup;
DROP TABLE IF EXISTS supinfo;

USE xcfp_user;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS payment_record;
DROP TABLE IF EXISTS orderinfo;
DROP TABLE IF EXISTS user_address;
DROP TABLE IF EXISTS admin_user;
DROP TABLE IF EXISTS user;
