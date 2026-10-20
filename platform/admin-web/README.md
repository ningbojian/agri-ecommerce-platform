# xcfp 管理后台

Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios 实现的 Web 管理端，对接 xcfp 的三个 Spring Boot 服务。

## 启动

前置：Node.js 18+，MySQL 已初始化，8201/8202/8203 三个服务已启动。

```bash
cd platform/admin-web
npm install
npm run dev
```

打开 `http://127.0.0.1:5173`，使用演示管理员账号：

```text
admin / admin123
```

生产构建：

```bash
npm run build     # 输出到 dist/
npm run preview   # 本地预览构建产物
```

## 接口代理

开发服务器在 `vite.config.js` 中配置了三个代理，前端统一使用相对路径：

| 前缀 | 目标服务 | 用途 |
|---|---|---|
| `/pro-api` | `http://127.0.0.1:8201` | 商品、分类、资讯 |
| `/user-api` | `http://127.0.0.1:8202` | 管理员登录、订单、用户、反馈 |
| `/sup-api` | `http://127.0.0.1:8203` | 供应商 |

部署 `dist/` 时需要由 Nginx/网关做同样的路径转发，或修改 `src/api/http.js` 中的 `baseURL`。

## 功能页面

| 路由 | 页面 | 功能 |
|---|---|---|
| `/login` | 登录 | 管理员登录，失败提示 |
| `/products` | 商品管理 | 分页/关键词/分类/状态筛选，新增、编辑、上下架、审核、删除 |
| `/categories` | 分类管理 | 列表、新增、编辑、删除（分类下有商品时后端拒绝） |
| `/articles` | 资讯管理 | 分页/标题/状态筛选，新增、编辑、删除 |
| `/orders` | 订单管理 | 状态/订单号筛选，详情抽屉，已支付订单发货 |
| `/users` | 用户管理 | 手机号/昵称/状态筛选（不展示密码） |
| `/suppliers` | 供应商管理 | 新增、编辑、审核、启停、删除 |
| `/feedback` | 反馈管理 | 按状态筛选、回复/追评 |

## 登录态与错误处理

- 登录成功后，`token` 与管理员信息存入 `localStorage`（`xcfp_admin_token`、`xcfp_admin_user`）。
- 路由守卫：未登录访问管理页会重定向到 `/login`；已登录访问登录页会跳回商品页。
- Axios 拦截器自动附带 `Authorization: Bearer <token>`。
- 业务码 `208` / HTTP `401`：清除本地会话并跳回登录页；其他错误统一弹 Toast。
- `Result.code === 200` 时直接返回 `data`，各页面无需重复解包。

> 生产环境建议改为 HttpOnly Cookie + CSRF 防护，并收紧后端 CORS/代理配置。当前实现面向本地演示。

## 目录

```text
src/
├─ main.js / App.vue / styles.css
├─ router/index.js          # 路由与登录守卫
├─ stores/auth.js           # Pinia 登录态
├─ api/
│  ├─ http.js               # 三个 axios 实例与错误拦截
│  ├─ user.js               # 登录/订单/用户/反馈
│  ├─ product.js            # 商品/分类/资讯
│  └─ sup.js                # 供应商
├─ layout/AdminLayout.vue   # 侧边菜单 + 顶栏退出
└─ views/                   # 登录与七个管理页
```

## 说明与限制

- 图片字段填 URL，不提供上传/OSS。
- 不做细粒度 RBAC，管理员均可访问全部菜单。
- 供应商密码仅用于新增或重置，编辑时留空表示不修改；接口不会返回密码。
- 后台没有删除订单/退款功能，与后端订单状态机保持一致。
- 不包含自动化测试配置；本地已用无头浏览器验证登录、七个页面渲染、商品/分类/资讯/供应商增删、反馈回复、订单发货与详情抽屉。
