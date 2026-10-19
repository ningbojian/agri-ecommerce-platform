-- Synthetic demo data only. Do not use real phone numbers or passwords.

USE xcfp_sup;
INSERT INTO supinfo (id, suptel, suppass, secode, supname, cardnum, signkey, is_delete)
VALUES (1, '13800000000', 'demo-pass', 'demo-secode', '演示助农供应商', 'DEMO-CARD-001', 'demo-sign-key', 0)
ON DUPLICATE KEY UPDATE supname = VALUES(supname);

USE xcfp_pro;
INSERT INTO product_info
  (id, proname, supid, supname, delivery, price, publish_status, audit_status, descript, picurl, sort, is_delete)
VALUES
  (1, '演示助农苹果 5kg', 1, '演示助农供应商', '包邮', 39.90, 1, 1, '课程项目演示商品', 'https://placehold.co/600x400?text=Agri+Apple', '1', 0),
  (2, '演示助农茶叶 250g', 1, '演示助农供应商', '包邮', 59.90, 1, 1, '课程项目演示商品', 'https://placehold.co/600x400?text=Agri+Tea', '2', 0),
  (3, '演示助农杂粮礼盒', 1, '演示助农供应商', '包邮', 79.00, 1, 1, '课程项目演示商品', 'https://placehold.co/600x400?text=Agri+Grain', '3', 0),
  (4, '演示助农水果组合', 1, '演示助农供应商', '包邮', 49.90, 1, 1, '课程项目演示商品', 'https://placehold.co/600x400?text=Agri+Fruit', '4', 0)
ON DUPLICATE KEY UPDATE proname = VALUES(proname);

USE xcfp_user;
INSERT INTO user (id, tel, password)
VALUES (1, '13900000000', 'demo123')
ON DUPLICATE KEY UPDATE password = VALUES(password);

INSERT INTO user_address (id, userid, address, addtel, addname, is_delete)
VALUES (1, 1, '上海市演示区示例路 1 号', '13900000000', '演示用户', 0)
ON DUPLICATE KEY UPDATE address = VALUES(address);

INSERT INTO orderinfo (id, userid, supid, orderid, proname, supname, price, pay, num, addid, state, express, is_delete)
VALUES (1, 1, 1, 'DEMO-ORDER-001', '演示助农苹果 5kg', '演示助农供应商', 39.90, 39.90, 1, 1, 'created', NULL, 0)
ON DUPLICATE KEY UPDATE state = VALUES(state);
