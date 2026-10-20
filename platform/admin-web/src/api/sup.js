import { supHttp } from './http'

export const getSupplierPage = params => supHttp.get('/admin/sup/page', { params })
export const getSupplier = id => supHttp.get(`/admin/sup/${id}`)
export const saveSupplier = data => supHttp.post('/admin/sup/save', data)
export const updateSupplier = data => supHttp.post('/admin/sup/update', data)
export const deleteSupplier = id => supHttp.delete(`/admin/sup/${id}`)
export const auditSupplier = (id, auditStatus) =>
  supHttp.post(`/admin/sup/${id}/audit`, { auditStatus })
export const setSupplierStatus = (id, status) =>
  supHttp.post(`/admin/sup/${id}/status`, { status })
