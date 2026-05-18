/**
 * phatService.ts — Service quản lý Phiếu phạt.
 */
import apiClient from './apiClient'
import { useAuthStore } from '@/stores/auth'
import type { PageResponse } from '@/types/common'
import type { PhieuPhat, TaoPhieuPhatRequest, ThanhToanRequest } from '@/types/phat'

export const phatService = {
  // Existing methods
  danhSach: (page: number = 0, size: number = 10, sortBy: string = 'ngayTao', direction: string = 'desc') => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy,
      direction
    })
    return apiClient.get<PageResponse<PhieuPhat>>(`/api/v1/phieu-muon/phieu-phat?${params.toString()}`)
  },

  taoCai: (body: TaoPhieuPhatRequest) =>
    apiClient.post<PhieuPhat>('/api/v1/phieu-muon/phieu-phat', body),

  thanhToan: (id: string, body?: ThanhToanRequest) =>
    apiClient.patch<PhieuPhat>(`/api/v1/phieu-muon/phieu-phat/${id}/thanh-toan`, body),

  // Requested English methods
  getAll: (page: number = 0, size: number = 10) => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString()
    })
    return apiClient.get<PageResponse<PhieuPhat>>(`/api/v1/phieu-muon/phieu-phat?${params.toString()}`)
  },

  getMy: () => {
    const authStore = useAuthStore()
    const maNguoiDung = authStore.thongTinNguoiDung?.maNguoiDung
    return apiClient.get<PhieuPhat[]>(`/api/v1/phieu-muon/phieu-phat/nguoi-dung/${maNguoiDung}`)
  },

  pay: (phatId: string) => {
    return apiClient.patch<PhieuPhat>(`/api/v1/phieu-muon/phieu-phat/${phatId}/thanh-toan`)
  }
}

export default phatService