#!/usr/bin/env node
/**
 * 本地主流程冒烟测试。
 *
 * 前置：
 *   1. 已按 sql/reset.sql -> schema.sql -> seed.sql 初始化数据库
 *   2. 三个服务已启动（默认 8201/8202/8203）
 *
 * 用法：
 *   node scripts/smoke-test.mjs
 *   可用 XCFP_PRO_URL / XCFP_USER_URL / XCFP_SUP_URL 覆盖服务地址。
 */

const PRO = process.env.XCFP_PRO_URL || 'http://127.0.0.1:8201';
const USER = process.env.XCFP_USER_URL || 'http://127.0.0.1:8202';
const SUP = process.env.XCFP_SUP_URL || 'http://127.0.0.1:8203';

let passed = 0;
const failures = [];

function check(condition, label, detail) {
  if (condition) {
    passed += 1;
    console.log(`  ✓ ${label}`);
    return;
  }
  const message = detail ? `${label} -> ${detail}` : label;
  failures.push(message);
  console.error(`  ✗ ${message}`);
}

async function call(base, method, path, { token, body, headers = {}, raw = false } = {}) {
  const response = await fetch(base + path, {
    method,
    headers: {
      ...(body === undefined ? {} : { 'Content-Type': 'application/json' }),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...headers,
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  let payload = null;
  const text = await response.text();
  try {
    payload = text ? JSON.parse(text) : null;
  } catch {
    payload = text;
  }
  return raw ? { status: response.status, payload } : payload;
}

function expectOk(payload, label) {
  check(payload && payload.code === 200, label, JSON.stringify(payload));
  return payload && payload.data;
}

async function main() {
  console.log('1) 公开商品/分类/资讯');
  const categories = expectOk(await call(PRO, 'GET', '/app/product/categories'), '获取分类');
  check(Array.isArray(categories) && categories.length >= 8, '分类数量 >= 8', String(categories?.length));

  const productPage = expectOk(
    await call(PRO, 'GET', '/app/product/page?current=1&limit=5'),
    '商品分页',
  );
  check(productPage.total >= 8 && productPage.records.length > 0, '商品分页有数据');
  const product = productPage.records[0];
  check(product.price !== undefined && product.stock !== undefined, '商品包含价格/库存');

  const articlePage = expectOk(await call(PRO, 'GET', '/app/article/page?current=1&limit=5'), '资讯分页');
  check(articlePage.total >= 5, '资讯数量 >= 5');
  const article = expectOk(await call(PRO, 'GET', `/app/article/${articlePage.records[0].id}`), '资讯详情');
  check(Boolean(article.content), '资讯详情包含正文');

  console.log('2) 注册 / 登录 / 个人资料');
  const tel = `138${String(Date.now()).slice(-8)}`;
  const registered = expectOk(
    await call(USER, 'POST', '/app/auth/register', {
      body: { tel, password: 'smoke123', nickname: '冒烟用户' },
    }),
    '注册',
  );
  check(Boolean(registered.token), '注册返回 token');
  check(registered.user.tel === tel && !('password' in registered.user), '注册响应不暴露密码');
  const userToken = registered.token;

  const login = expectOk(
    await call(USER, 'POST', '/app/auth/login', { body: { tel, password: 'smoke123' } }),
    '登录',
  );
  check(Boolean(login.token), '登录返回 token');

  const profile = expectOk(await call(USER, 'GET', '/app/user/profile', { token: userToken }), '获取个人资料');
  check(profile.tel === tel, '个人资料手机号正确');
  const updatedProfile = expectOk(
    await call(USER, 'PUT', '/app/user/profile', {
      token: userToken,
      body: { nickname: '冒烟用户改', avatar: '' },
    }),
    '修改个人资料',
  );
  check(updatedProfile.nickname === '冒烟用户改', '昵称已更新');

  console.log('3) 地址管理');
  const address = expectOk(
    await call(USER, 'POST', '/app/address/save', {
      token: userToken,
      body: { addname: '张三', addtel: '13900001111', address: '测试省测试市测试路 1 号', isDefault: true },
    }),
    '新增地址',
  );
  check(address.isDefault === 1, '首个地址自动设为默认');
  const updatedAddress = expectOk(
    await call(USER, 'POST', '/app/address/update', {
      token: userToken,
      body: { id: address.id, addname: '李四', addtel: '13900002222', address: '测试省测试市测试路 2 号' },
    }),
    '编辑地址',
  );
  check(updatedAddress.addname === '李四', '地址确实被更新');
  const addressList = expectOk(await call(USER, 'GET', '/app/address/list', { token: userToken }), '地址列表');
  check(addressList.length === 1, '地址列表数量为 1');
  const defaultAddress = expectOk(await call(USER, 'GET', '/app/address/default', { token: userToken }), '默认地址');
  check(defaultAddress.id === address.id, '默认地址正确');

  console.log('4) 下单 / 支付 / 发货 / 确认收货');
  const stockBefore = (expectOk(await call(PRO, 'GET', `/app/product/${product.id}`), '下单前商品详情')).stock;
  const order = expectOk(
    await call(USER, 'POST', '/app/order/create', {
      token: userToken,
      body: { productId: product.id, num: 2, addressId: address.id },
    }),
    '创建订单',
  );
  const expectedPay = Number(product.price) * 2;
  check(Number(order.pay) === Number(expectedPay.toFixed(2)), '金额由服务端按单价×数量计算',
    `pay=${order.pay}, expected=${expectedPay}`);
  check(order.state === 'created', '新订单状态为 created');
  check(order.receiverName === '李四', '订单保存地址快照');
  const stockAfter = (expectOk(await call(PRO, 'GET', `/app/product/${product.id}`), '下单后商品详情')).stock;
  check(stockAfter === stockBefore - 2, '下单后库存减少 2', `${stockBefore} -> ${stockAfter}`);

  const paid = expectOk(
    await call(USER, 'POST', `/app/order/${order.orderid}/pay`, { token: userToken }),
    '模拟支付',
  );
  check(paid.state === 'paid', '支付后状态为 paid');
  const paidAgain = expectOk(
    await call(USER, 'POST', `/app/order/${order.orderid}/pay`, { token: userToken }),
    '重复支付幂等',
  );
  check(paidAgain.state === 'paid', '重复支付仍为 paid');

  const admin = expectOk(
    await call(USER, 'POST', '/admin/auth/login', { body: { username: 'admin', password: 'admin123' } }),
    '管理员登录',
  );
  check(Boolean(admin.token) && !('password' in admin.admin), '管理员登录返回 token 且不含密码');
  const adminToken = admin.token;

  const adminUsers = expectOk(
    await call(USER, 'GET', '/admin/user/page?current=1&limit=100', { token: adminToken }),
    '管理端用户分页',
  );
  check(JSON.stringify(adminUsers).includes(tel), '管理端能查到新用户');
  check(!JSON.stringify(adminUsers).includes('$2'), '用户列表不返回密码哈希');

  const adminOrderPage = expectOk(
    await call(USER, 'GET', `/admin/order/page?current=1&limit=10&orderid=${order.orderid}`, { token: adminToken }),
    '管理端订单分页',
  );
  check(adminOrderPage.total === 1, '管理端能按订单号查到订单');
  const shipped = expectOk(
    await call(USER, 'POST', `/admin/order/${order.orderid}/ship`, {
      token: adminToken,
      body: { express: 'SF1234567890' },
    }),
    '发货',
  );
  check(shipped.state === 'shipped' && shipped.express === 'SF1234567890', '发货后状态为 shipped');
  const completed = expectOk(
    await call(USER, 'POST', `/app/order/${order.orderid}/confirm`, { token: userToken }),
    '确认收货',
  );
  check(completed.state === 'completed', '确认收货后状态为 completed');

  console.log('5) 取消订单恢复库存');
  const stockBeforeCancel = (expectOk(await call(PRO, 'GET', `/app/product/${product.id}`), '取消前库存')).stock;
  const order2 = expectOk(
    await call(USER, 'POST', '/app/order/create', {
      token: userToken,
      body: { productId: product.id, num: 1, addressId: address.id },
    }),
    '再下一单',
  );
  const cancelled = expectOk(
    await call(USER, 'POST', `/app/order/${order2.orderid}/cancel`, { token: userToken }),
    '取消订单',
  );
  check(cancelled.state === 'cancelled', '取消后状态为 cancelled');
  const stockAfterCancel = (expectOk(await call(PRO, 'GET', `/app/product/${product.id}`), '取消后库存')).stock;
  check(stockAfterCancel === stockBeforeCancel, '取消订单后库存恢复', `${stockBeforeCancel} -> ${stockAfterCancel}`);

  console.log('6) 反馈');
  const feedback = expectOk(
    await call(USER, 'POST', '/app/feedback', {
      token: userToken,
      body: { content: '这是一条冒烟测试反馈', contact: tel },
    }),
    '提交反馈',
  );
  const myFeedback = expectOk(await call(USER, 'GET', '/app/feedback/my', { token: userToken }), '我的反馈');
  check(myFeedback.total === 1, '我的反馈数量为 1');
  const adminFeedback = expectOk(
    await call(USER, 'GET', '/admin/feedback/page?current=1&limit=10', { token: adminToken }),
    '管理端反馈分页',
  );
  check(adminFeedback.total >= 1, '管理端能看到反馈');
  const replied = expectOk(
    await call(USER, 'POST', `/admin/feedback/${feedback.id}/reply`, {
      token: adminToken,
      body: { reply: '感谢反馈，已处理' },
    }),
    '回复反馈',
  );
  check(replied.status === 1 && replied.reply === '感谢反馈，已处理', '反馈已回复');

  console.log('7) 供应商管理');
  const supplierTel = `137${String(Date.now()).slice(-8)}`;
  const supplier = expectOk(
    await call(SUP, 'POST', '/admin/sup/save', {
      token: adminToken,
      body: { suptel: supplierTel, supname: '冒烟供应商', suppass: 'supplier123', descript: '测试' },
    }),
    '新增供应商',
  );
  check(!('suppass' in supplier) && !('signkey' in supplier) && !('secode' in supplier),
    '供应商响应不暴露密码/密钥');
  const supplierPage = expectOk(
    await call(SUP, 'GET', `/admin/sup/page?current=1&limit=10&keyword=${encodeURIComponent('冒烟供应商')}`, {
      token: adminToken,
    }),
    '供应商分页',
  );
  check(supplierPage.total === 1, '供应商分页可查询新供应商');
  const audited = expectOk(
    await call(SUP, 'POST', `/admin/sup/${supplier.id}/audit`, {
      token: adminToken,
      body: { auditStatus: 1 },
    }),
    '审核供应商',
  );
  check(audited.auditStatus === 1, '供应商审核通过');
  const supplierUpdated = expectOk(
    await call(SUP, 'POST', '/admin/sup/update', {
      token: adminToken,
      body: { id: supplier.id, suptel: supplierTel, supname: '冒烟供应商改' },
    }),
    '编辑供应商',
  );
  check(supplierUpdated.supname === '冒烟供应商改', '供应商名称已更新');
  expectOk(await call(SUP, 'DELETE', `/admin/sup/${supplier.id}`, { token: adminToken }), '删除供应商');

  console.log('8) 管理端商品/分类/资讯');
  const adminProducts = expectOk(
    await call(PRO, 'GET', '/admin/product/page?current=1&limit=5', { token: adminToken }),
    '管理端商品分页',
  );
  check(adminProducts.total >= 8, '管理端商品分页有数据');
  const adminCategories = expectOk(
    await call(PRO, 'GET', '/admin/category/list', { token: adminToken }),
    '管理端分类列表',
  );
  check(adminCategories.length >= 8, '管理端分类列表有数据');
  const adminArticles = expectOk(
    await call(PRO, 'GET', '/admin/article/page?current=1&limit=5', { token: adminToken }),
    '管理端资讯分页',
  );
  check(adminArticles.total >= 5, '管理端资讯分页有数据');

  console.log('9) 鉴权与归属校验');
  const anonymous = await call(USER, 'GET', '/app/order/page', { raw: true });
  check(anonymous.status === 401 && anonymous.payload?.code === 208, '未登录访问订单返回 401/208',
    `status=${anonymous.status}, code=${anonymous.payload?.code}`);

  const wrongRole = await call(USER, 'GET', '/admin/order/page', { token: userToken, raw: true });
  check(wrongRole.status === 403 && wrongRole.payload?.code === 209, '普通用户访问管理端返回 403/209',
    `status=${wrongRole.status}, code=${wrongRole.payload?.code}`);

  const otherTel = `136${String(Date.now()).slice(-8)}`;
  const other = expectOk(
    await call(USER, 'POST', '/app/auth/register', {
      body: { tel: otherTel, password: 'smoke123' },
    }),
    '注册另一个用户',
  );
  const forbiddenAddress = await call(USER, 'POST', '/app/address/update', {
    token: other.token,
    body: { id: address.id, addname: '王五', addtel: '13900003333', address: '别人的地址' },
    raw: true,
  });
  check(forbiddenAddress.payload?.code === 209, '用户不能修改他人地址',
    `code=${forbiddenAddress.payload?.code}`);

  const forbiddenOrder = await call(USER, 'GET', `/app/order/${order.orderid}`, {
    token: other.token,
    raw: true,
  });
  check(forbiddenOrder.payload?.code === 209, '用户不能查看他人订单',
    `code=${forbiddenOrder.payload?.code}`);

  const internalNoToken = await call(PRO, 'POST', '/internal/product/reserve', {
    body: { productId: product.id, num: 1 },
    raw: true,
  });
  check(internalNoToken.status === 401 && internalNoToken.payload?.code === 208,
    '内部接口缺少 token 返回 401/208',
    `status=${internalNoToken.status}, code=${internalNoToken.payload?.code}`);

  const unavailable = await call(PRO, 'GET', '/app/product/page?current=1&limit=1&keyword=不存在的商品', { raw: true });
  check(unavailable.payload?.code === 200 && unavailable.payload.data.total === 0, '商品搜索无结果正常返回空页');

  console.log('\n============================');
  console.log(`通过 ${passed} 项，失败 ${failures.length} 项`);
  if (failures.length > 0) {
    failures.forEach((item) => console.error(` - ${item}`));
    process.exitCode = 1;
  }
}

main().catch((error) => {
  console.error('冒烟测试中断:', error);
  process.exitCode = 1;
});
