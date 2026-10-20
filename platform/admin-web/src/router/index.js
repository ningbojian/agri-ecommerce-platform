import { createRouter, createWebHashHistory } from 'vue-router'
import AdminLayout from '../layout/AdminLayout.vue'
import Login from '../views/Login.vue'
import ProductList from '../views/ProductList.vue'
import CategoryList from '../views/CategoryList.vue'
import ArticleList from '../views/ArticleList.vue'
import OrderList from '../views/OrderList.vue'
import UserList from '../views/UserList.vue'
import SupplierList from '../views/SupplierList.vue'
import FeedbackList from '../views/FeedbackList.vue'
import { getToken } from '../utils/session'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/login', name: 'login', component: Login, meta: { public: true } },
    {
      path: '/',
      component: AdminLayout,
      redirect: '/products',
      children: [
        { path: 'products', name: 'products', component: ProductList, meta: { title: '商品管理' } },
        { path: 'categories', name: 'categories', component: CategoryList, meta: { title: '分类管理' } },
        { path: 'articles', name: 'articles', component: ArticleList, meta: { title: '资讯管理' } },
        { path: 'orders', name: 'orders', component: OrderList, meta: { title: '订单管理' } },
        { path: 'users', name: 'users', component: UserList, meta: { title: '用户管理' } },
        { path: 'suppliers', name: 'suppliers', component: SupplierList, meta: { title: '供应商管理' } },
        { path: 'feedback', name: 'feedback', component: FeedbackList, meta: { title: '反馈管理' } }
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/products' }
  ]
})

router.beforeEach(to => {
  if (to.meta.public) {
    if (to.path === '/login' && getToken()) return '/products'
    return true
  }
  if (!getToken()) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
