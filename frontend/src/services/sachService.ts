/**
 * sachService.ts — Service quản lý Đầu sách.
 */
import apiClient from './apiClient'
import type { PageResponse } from '@/types/common'
import type { Sach, TaoSachRequest } from '@/types/sach'

const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

function formatSachHinhAnh(sach: Sach): Sach {
  if (!sach) return sach
  if (sach.danhSachHinhAnhUrl && Array.isArray(sach.danhSachHinhAnhUrl)) {
    sach.danhSachHinhAnhUrl = sach.danhSachHinhAnhUrl.map((url: string) => {
      if (url && url.startsWith('/api/v1/files/')) {
        return `${baseUrl}${url}`
      }
      return url
    })
  }
  return sach
}

function formatPageHinhAnh(page: PageResponse<Sach>): PageResponse<Sach> {
  if (page && page.content && Array.isArray(page.content)) {
    page.content = page.content.map(formatSachHinhAnh)
  }
  return page
}

export const sachService = {
  // Original methods
  danhSach: async (page: number = 0, size: number = 10, keyword: string = '', sortBy: string = '', direction: string = '') => {
    const params = new URLSearchParams()
    params.append('page', page.toString())
    params.append('size', size.toString())
    if (keyword) params.append('keyword', keyword)
    if (sortBy) params.append('sortBy', sortBy)
    if (direction) params.append('direction', direction)
    const res = await apiClient.get<PageResponse<Sach>>(`/api/v1/sach?${params.toString()}`)
    return formatPageHinhAnh(res)
  },

  layMotCuon: async (id: number) => {
    const res = await apiClient.get<Sach>(`/api/v1/sach/${id}`)
    return formatSachHinhAnh(res)
  },

  taoCai: async (body: TaoSachRequest) => {
    const res = await apiClient.post<Sach>('/api/v1/sach', body)
    return formatSachHinhAnh(res)
  },

  capNhat: async (id: number, body: TaoSachRequest) => {
    const res = await apiClient.put<Sach>(`/api/v1/sach/${id}`, body)
    return formatSachHinhAnh(res)
  },

  xoa: (id: number) =>
    apiClient.delete<void>(`/api/v1/sach/${id}`),

  uploadAnhBia: async (id: number, file: File) => {
    // 1. Delete any existing cover images first to avoid duplicates
    try {
      const list = await apiClient.get<any[]>(`/api/v1/hinh-anh-sach/sach/${id}`)
      if (list && list.length > 0) {
        for (const img of list) {
          if (img.maHinhAnh) {
            await apiClient.delete(`/api/v1/hinh-anh-sach/${img.maHinhAnh}`)
          }
        }
      }
    } catch (err) {
      console.warn('Could not clear existing covers:', err)
    }

    // 2. Upload the new file to /api/v1/files/upload
    const formData = new FormData()
    formData.append('file', file)
    const uploadRes = await apiClient.upload<{ url: string }>('/api/v1/files/upload', formData)
    const fileUrl = uploadRes.url

    // 3. Register the new cover image as BIA_TRUOC
    return apiClient.post<any>('/api/v1/hinh-anh-sach', {
      duongDan: fileUrl,
      loaiHinhAnh: 'BIA_TRUOC',
      thuTuHienThi: 0,
      maSach: id
    })
  },

  xoaAnhBia: async (id: number) => {
    // Fetch all cover images associated with the book
    const list = await apiClient.get<any[]>(`/api/v1/hinh-anh-sach/sach/${id}`)
    if (list && list.length > 0) {
      for (const img of list) {
        if (img.maHinhAnh) {
          await apiClient.delete<void>(`/api/v1/hinh-anh-sach/${img.maHinhAnh}`)
        }
      }
    }
  },

  /** Advanced search & filter */
  advancedSearch: async (params: { page?: number; size?: number; keyword?: string; maTheLoai?: number; sortBy?: string; direction?: string }) => {
    const p = new URLSearchParams()
    if (params.page !== undefined) p.append('page', params.page.toString())
    if (params.size !== undefined) p.append('size', params.size.toString())
    if (params.keyword) p.append('keyword', params.keyword)
    if (params.maTheLoai) p.append('maTheLoai', params.maTheLoai.toString())
    if (params.sortBy) p.append('sortBy', params.sortBy)
    if (params.direction) p.append('direction', params.direction)
    const res = await apiClient.get<PageResponse<Sach>>(`/api/v1/sach/search?${p.toString()}`)
    return formatPageHinhAnh(res)
  },

  // Requested English interface
  getAll: async (params?: any) => {
    const res = await apiClient.get<PageResponse<Sach>>('/api/v1/sach', { params })
    return formatPageHinhAnh(res)
  },

  getById: async (id: number) => {
    const res = await apiClient.get<Sach>(`/api/v1/sach/${id}`)
    return formatSachHinhAnh(res)
  },

  create: async (body: TaoSachRequest) => {
    const res = await apiClient.post<Sach>('/api/v1/sach', body)
    return formatSachHinhAnh(res)
  },

  update: async (id: number, body: TaoSachRequest) => {
    const res = await apiClient.put<Sach>(`/api/v1/sach/${id}`, body)
    return formatSachHinhAnh(res)
  },

  delete: (id: number) => {
    return apiClient.delete<void>(`/api/v1/sach/${id}`)
  },

  search: async (keyword: string, page: number = 0, size: number = 10) => {
    const res = await apiClient.get<PageResponse<Sach>>('/api/v1/sach', { params: { keyword, page, size } })
    return formatPageHinhAnh(res)
  }
}

export default sachService