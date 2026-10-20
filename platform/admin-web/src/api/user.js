import { userHttp } from './http'

// 认证
export const adminLogin = data => userHttp.post('/admin/auth/login', data)

// 用户
export const getUserPage = params => userHttp.get('/admin/user/page', { params })

// 订单
export const getOrderPage = params => userHttp.get('/admin/order/page', { params })
export const getOrderDetail = orderid => userHttp.get(`/admin/order/${orderid}`)
export const shipOrder = (orderid, data) => userHttp.post(`/admin/order/${orderid}/ship`, data)

// 反馈
export const getFeedbackPage = params => userHttp.get('/admin/feedback/page', { params })
export const replyFeedback = (id, data) => userHttp.post(`/admin/feedback/${id}/reply`, data)
