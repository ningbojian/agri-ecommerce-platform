-- 演示数据（虚构手机号/密码/商品），可重复执行。
-- 密码均为 BCrypt：
--   用户 13900000000 / demo123
--   管理员 admin / admin123
--   供应商 13800000000 / demo-pass（本期无供应商登录）

USE xcfp_sup;
INSERT INTO supinfo
  (id, suptel, suppass, secode, supname, cardnum, signkey, logo, descript, audit_status, status, is_delete)
VALUES
  (1, '13800000000', '$2y$10$HDvck1iSCPZR30shgkW6FO6PWY430e0mt8t27wok12GIDPlRLO.nu', NULL,
   '演示助农供应商', 'DEMO-CARD-001', 'demo-internal-sign-key', 'https://placehold.co/300x300?text=Supplier',
   '课程演示用供应商，销售本地农产品。', 1, 1, 0)
ON DUPLICATE KEY UPDATE
  supname = VALUES(supname), audit_status = VALUES(audit_status), status = VALUES(status);

USE xcfp_pro;
INSERT INTO product_category (id, name, icon, sort_order, status, is_delete)
VALUES
  (1, '水果', NULL, 1, 1, 0),
  (2, '蔬菜', NULL, 2, 1, 0),
  (3, '肉类', NULL, 3, 1, 0),
  (4, '粮油', NULL, 4, 1, 0),
  (5, '面点', NULL, 5, 1, 0),
  (6, '冷链', NULL, 6, 1, 0),
  (7, '零食', NULL, 7, 1, 0),
  (8, '酒水', NULL, 8, 1, 0)
ON DUPLICATE KEY UPDATE name = VALUES(name), sort_order = VALUES(sort_order), status = VALUES(status);

INSERT INTO product_info
  (id, proname, supid, supname, category_id, delivery, price, stock, sales, unit,
   publish_status, audit_status, descript, picurl, sort, is_delete)
VALUES
  (1, '助农苹果 5kg', 1, '演示助农供应商', 1, '产地直发', 39.90, 100, 0, '箱', 1, 1,
   '脆甜多汁的当季苹果，产地采摘后直接发货。', 'https://placehold.co/600x400?text=Apple', '1', 0),
  (2, '有机番茄 2.5kg', 1, '演示助农供应商', 2, '产地直发', 19.90, 100, 0, '箱', 1, 1,
   '自然成熟的有机番茄，酸甜适口。', 'https://placehold.co/600x400?text=Tomato', '2', 0),
  (3, '散养土鸡 1只', 1, '演示助农供应商', 3, '冷链发货', 69.00, 100, 0, '只', 1, 1,
   '农户散养土鸡，冷鲜配送。', 'https://placehold.co/600x400?text=Chicken', '3', 0),
  (4, '高山菜籽油 5L', 1, '演示助农供应商', 4, '包邮', 89.00, 100, 0, '桶', 1, 1,
   '高山油菜籽物理压榨，香味浓郁。', 'https://placehold.co/600x400?text=Oil', '4', 0),
  (5, '手工挂面 2kg', 1, '演示助农供应商', 5, '包邮', 25.90, 100, 0, '袋', 1, 1,
   '传统手工制作，久煮不糊。', 'https://placehold.co/600x400?text=Noodle', '5', 0),
  (6, '鲜冻虾仁 500g', 1, '演示助农供应商', 6, '冷链发货', 59.90, 100, 0, '袋', 1, 1,
   '急冻锁鲜虾仁，解冻即可烹饪。', 'https://placehold.co/600x400?text=Shrimp', '6', 0),
  (7, '高山绿茶 250g', 1, '演示助农供应商', 7, '包邮', 49.90, 100, 0, '罐', 1, 1,
   '高山云雾茶园采摘，清香回甘。', 'https://placehold.co/600x400?text=Tea', '7', 0),
  (8, '农家米酒 1L', 1, '演示助农供应商', 8, '包邮', 35.00, 100, 0, '瓶', 1, 1,
   '传统工艺酿造，米香醇厚。', 'https://placehold.co/600x400?text=Rice+Wine', '8', 0)
ON DUPLICATE KEY UPDATE
  proname = VALUES(proname), category_id = VALUES(category_id), price = VALUES(price),
  stock = VALUES(stock), unit = VALUES(unit), publish_status = VALUES(publish_status);

INSERT INTO article (id, title, summary, content, cover_url, author, status, publish_time, is_delete)
VALUES
  (1, '乡村好物如何走出大山',
   '从选品、包装到物流，了解农产品上行的几个关键环节。',
   '农产品上行不是简单地把商品挂到网上。\n\n第一步是选品：优先选择耐运输、品质稳定、有地方特色的产品。\n第二步是标准化：统一规格、重量和包装，减少售后纠纷。\n第三步是物流：根据保质期选择常温或冷链，并在详情页写清发货时效。\n\n平台会持续完善商品和订单能力，帮助本地农户把好产品卖得更远。',
   'https://placehold.co/800x450?text=Article+1', '平台运营', 1, DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
  (2, '春季果园管理小知识',
   '春季修剪、施肥和病虫害防治的简单要点。',
   '春季是果树管理的关键时期。\n\n修剪：及时剪除病枝、枯枝，保持树冠通风透光。\n施肥：根据树龄和土壤情况补充有机肥，避免过量施用氮肥。\n病虫害：以预防为主，发现异常及时处理，优先采用物理和生物防治。\n\n以上内容仅供演示，实际生产请咨询当地农技人员。',
   'https://placehold.co/800x450?text=Article+2', '平台运营', 1, DATE_SUB(NOW(), INTERVAL 4 DAY), 0),
  (3, '农产品冷链运输指南',
   '哪些产品需要冷链，发货时要注意什么。',
   '肉类、水产和部分鲜食农产品需要冷链运输。\n\n发货前应预冷，并使用保温箱和冰袋控制温度。\n包装要防止挤压和渗漏，快递面单注明“生鲜”。\n消费者收货后应尽快冷藏或冷冻，发现问题及时拍照并联系售后。\n\n本平台演示订单中的“冷链发货”仅作为文案展示。',
   'https://placehold.co/800x450?text=Article+3', '平台运营', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
  (4, '电商助农：从田间到餐桌',
   '缩短流通环节，让农户和消费者都受益。',
   '传统流通环节多，农产品从田间到餐桌往往要经过多层批发。\n\n电商直连产地和消费者，可以减少中间环节、提高农户收益，也让消费者更容易了解产品来源。\n\n平台通过供应商管理、商品审核和订单履约，尽量保证信息真实、交易可追溯。',
   'https://placehold.co/800x450?text=Article+4', '平台运营', 1, DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
  (5, '平台下单与售后说明',
   '下单、支付、发货和售后流程说明。',
   '下单流程：选择商品 → 填写或选择收货地址 → 提交订单 → 完成支付。\n\n订单状态：待付款、待发货、待收货、已完成；待付款订单可以取消。\n\n售后：如商品有质量问题，请保留照片和快递面单，通过“意见反馈”联系平台。\n\n当前支付为演示用模拟支付，不会产生真实扣款。',
   'https://placehold.co/800x450?text=Article+5', '平台运营', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), 0)
ON DUPLICATE KEY UPDATE
  title = VALUES(title), summary = VALUES(summary), content = VALUES(content),
  cover_url = VALUES(cover_url), status = VALUES(status), publish_time = VALUES(publish_time);

USE xcfp_user;
INSERT INTO user (id, tel, password, nickname, avatar, status, is_delete)
VALUES
  (1, '13900000000', '$2y$10$tkSK0s.GCkRR5jzYmyLoTefSeCHCISwWccew6nEuNQPcJJbxbDaPe',
   '演示用户', NULL, 1, 0)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname), status = VALUES(status);

INSERT INTO admin_user (id, username, password, nickname, status)
VALUES
  (1, 'admin', '$2y$10$G8iZJFmMsTLmN17207CghuNnbS7YXFmRafdPwrNrK8Fp.q1eOfq1K', '平台管理员', 1)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname), status = VALUES(status);

INSERT INTO user_address (id, userid, address, addtel, addname, is_default, is_delete)
VALUES
  (1, 1, '上海市演示区示例路 1 号', '13900000000', '演示用户', 1, 0),
  (2, 1, '上海市演示区示例路 2 号', '13900000000', '演示用户（备用）', 0, 0)
ON DUPLICATE KEY UPDATE
  address = VALUES(address), addname = VALUES(addname), is_default = VALUES(is_default);

INSERT INTO orderinfo
  (id, userid, supid, orderid, product_id, proname, picurl, supname, price, pay, num, addid,
   receiver_name, receiver_tel, receiver_address, state, express,
   pay_time, ship_time, complete_time, cancel_time, is_delete)
VALUES
  (1, 1, 1, 'DEMO-ORDER-001', 1, '助农苹果 5kg', 'https://placehold.co/600x400?text=Apple',
   '演示助农供应商', 39.90, 39.90, 1, 1, '演示用户', '13900000000', '上海市演示区示例路 1 号',
   'created', NULL, NULL, NULL, NULL, NULL, 0),
  (2, 1, 1, 'DEMO-ORDER-002', 2, '有机番茄 2.5kg', 'https://placehold.co/600x400?text=Tomato',
   '演示助农供应商', 19.90, 39.80, 2, 1, '演示用户', '13900000000', '上海市演示区示例路 1 号',
   'paid', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, NULL, NULL, 0),
  (3, 1, 1, 'DEMO-ORDER-003', 3, '散养土鸡 1只', 'https://placehold.co/600x400?text=Chicken',
   '演示助农供应商', 69.00, 69.00, 1, 1, '演示用户', '13900000000', '上海市演示区示例路 1 号',
   'shipped', '顺丰 SF1234567890', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, 0),
  (4, 1, 1, 'DEMO-ORDER-004', 4, '高山菜籽油 5L', 'https://placehold.co/600x400?text=Oil',
   '演示助农供应商', 89.00, 89.00, 1, 1, '演示用户', '13900000000', '上海市演示区示例路 1 号',
   'completed', '中通 ZTO9876543210', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY),
   DATE_SUB(NOW(), INTERVAL 7 DAY), NULL, 0),
  (5, 1, 1, 'DEMO-ORDER-005', 5, '手工挂面 2kg', 'https://placehold.co/600x400?text=Noodle',
   '演示助农供应商', 25.90, 25.90, 1, 1, '演示用户', '13900000000', '上海市演示区示例路 1 号',
   'cancelled', NULL, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 6 DAY), 0)
ON DUPLICATE KEY UPDATE state = VALUES(state), express = VALUES(express);

INSERT INTO payment_record (id, pay_no, orderid, userid, amount, channel, status, transaction_id, pay_time)
VALUES
  (1, 'PAY-DEMO-001', 'DEMO-ORDER-002', 1, 39.80, 'MOCK', 'SUCCESS', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)),
  (2, 'PAY-DEMO-002', 'DEMO-ORDER-004', 1, 89.00, 'MOCK', 'SUCCESS', NULL, DATE_SUB(NOW(), INTERVAL 10 DAY))
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO feedback (id, userid, content, contact, status, reply, reply_time, is_delete)
VALUES
  (1, 1, '希望增加商品搜索和更多支付方式。', '13900000000', 1,
   '感谢建议，搜索功能已支持关键词，支付方式会在后续版本补充。', DATE_SUB(NOW(), INTERVAL 1 DAY), 0)
ON DUPLICATE KEY UPDATE reply = VALUES(reply), status = VALUES(status);
