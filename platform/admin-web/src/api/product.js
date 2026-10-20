import { proHttp } from './http'

// 商品
export const getProductPage = params => proHttp.get('/admin/product/page', { params })
export const getProduct = id => proHttp.get(`/admin/product/${id}`)
export const saveProduct = data => proHttp.post('/admin/product/save', data)
export const updateProduct = (id, data) => proHttp.post(`/admin/product/update/${id}`, data)
export const deleteProduct = id => proHttp.delete(`/admin/product/${id}`)
export const publishProduct = (id, publishStatus) =>
  proHttp.post(`/admin/product/${id}/publish`, { publishStatus })
export const setProductStock = (id, stock) => proHttp.post(`/admin/product/${id}/stock`, { stock })
export const auditProduct = (id, auditStatus) =>
  proHttp.post(`/admin/product/${id}/audit`, { auditStatus })

// 分类
export const getCategories = () => proHttp.get('/admin/category/list')
export const saveCategory = data => proHttp.post('/admin/category/save', data)
export const updateCategory = (id, data) => proHttp.post(`/admin/category/update/${id}`, data)
export const deleteCategory = id => proHttp.delete(`/admin/category/${id}`)

// 资讯
export const getArticlePage = params => proHttp.get('/admin/article/page', { params })
export const getArticle = id => proHttp.get(`/admin/article/${id}`)
export const saveArticle = data => proHttp.post('/admin/article/save', data)
export const updateArticle = (id, data) => proHttp.post(`/admin/article/update/${id}`, data)
export const deleteArticle = id => proHttp.delete(`/admin/article/${id}`)
