/**
 * docGiaService.ts — Service quản lý Độc giả / Người dùng.
 */
import apiClient from './apiClient'
import type { PageResponse } from '@/types/common'
import type { NguoiDung, TaoNguoiDungExcelRequest, TaoNguoiDungRequest, SuaNguoiDungRequest } from '@/types/nguoidung'

export const docGiaService = {
  danhSach: (page: number = 0, size: number = 10, keyword: string = '', trangThai: string = '', sortBy: string = '', direction: string = '') => {
    const params = new URLSearchParams()
    params.append('page', page.toString())
    params.append('size', size.toString())
    if (keyword) params.append('keyword', keyword)
    if (trangThai && trangThai !== 'all') {
      let statusParam = ''
      if (trangThai === 'chua_kich_hoat') statusParam = 'CHUA_KICH_HOAT'
      else if (trangThai === 'da_kich_hoat') statusParam = 'HOAT_DONG'
      else if (trangThai === 'bi_khoa') statusParam = 'KHOA'
      
      if (statusParam) params.append('trangThai', statusParam)
    }
    if (sortBy) params.append('sortBy', sortBy)
    if (direction) params.append('direction', direction)
    return apiClient.get<PageResponse<NguoiDung>>(`/api/v1/nguoi-dung?${params.toString()}`)
  },

  taoCai: (body: TaoNguoiDungRequest) =>
    apiClient.post<NguoiDung>('/api/v1/nguoi-dung', body),

  capNhat: (id: string, body: SuaNguoiDungRequest) =>
    apiClient.put<NguoiDung>(`/api/v1/nguoi-dung/${id}`, body),

  xoa: (id: string) =>
    apiClient.delete<void>(`/api/v1/nguoi-dung/${id}`),

  khoa: (id: string) =>
    apiClient.patch<NguoiDung>(`/api/v1/nguoi-dung/${id}/trang-thai?trangThai=KHOA`),

  moKhoa: (id: string) =>
    apiClient.patch<NguoiDung>(`/api/v1/nguoi-dung/${id}/trang-thai?trangThai=HOAT_DONG`),

  kichHoat: (id: string) =>
    apiClient.patch<NguoiDung>(`/api/v1/nguoi-dung/${id}/trang-thai?trangThai=HOAT_DONG`),

  taoHangLoat: (danhSach: TaoNguoiDungExcelRequest[]) =>
    apiClient.post<{ thanhCong: number; thatBai: number; loi: Array<{ email: string; message: string }> }>(
      '/api/v1/admin/nguoi-dung/tao-hang-loat',
      { danhSach },
    ),

  importExcel: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.upload<any>('/api/v1/nguoi-dung/import-excel', formData).then((res) => {
      const rawList = res.danhSachLoi || res.loi || []
      const loi = rawList.map((item: any) => {
        if (typeof item === 'string') {
          return { email: 'Chi tiết', message: item }
        }
        return {
          email: item.email || 'Lỗi',
          message: item.message || item.msg || 'Không rõ nguyên nhân'
        }
      })
      return {
        thanhCong: res.thanhCong || 0,
        thatBai: res.thatBai || 0,
        loi
      }
    })
  },

  // Requested English methods
  getAll: (params?: any) => {
    const p = new URLSearchParams()
    if (params) {
      Object.entries(params).forEach(([key, val]) => {
        if (val !== undefined && val !== null) {
          p.append(key, String(val))
        }
      })
    }
    return apiClient.get<PageResponse<NguoiDung>>(`/api/v1/nguoi-dung?${p.toString()}`)
  },

  getById: (id: string) => {
    return apiClient.get<NguoiDung>(`/api/v1/nguoi-dung/${id}`)
  },

  getMyProfile: () => {
    return apiClient.get<NguoiDung>('/api/v1/nguoi-dung/me')
  },

  updateProfile: (body: SuaNguoiDungRequest) => {
    return apiClient.put<NguoiDung>('/api/v1/nguoi-dung/me', body)
  },

  getBorrowHistory: (id: string) => {
    return apiClient.get<any>(`/api/v1/doc-gia/${id}/lich-su-muon`)
  },

  /** Admin đổi mật khẩu không cần OTP */
  adminDoiMatKhau: (id: string, matKhauMoi: string) =>
    apiClient.patch<void>(`/api/v1/nguoi-dung/${id}/doi-mat-khau?matKhauMoi=${encodeURIComponent(matKhauMoi)}`, {}),
}

export default docGiaService