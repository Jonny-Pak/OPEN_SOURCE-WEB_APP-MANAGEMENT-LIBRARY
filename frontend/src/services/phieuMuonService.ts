/**
 * phieuMuonService.ts — Service quản lý Phiếu mượn và Trả sách.
 */
import type { PageResponse } from '@/types/common'

const BASE_URL = 'http://localhost:8080/api/v1/phieu-muon'

export interface MuonSachRequest {
  maNguoiDung: string
  danhSachMaSach: number[]
}

async function xuLyResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json().catch(() => ({}))
    throw error
  }
  if (response.status === 204) return {} as T
  return await response.json().catch(() => ({}))
}

export const phieuMuonService = {
  /** Tạo phiếu mượn mới (Độc giả hoặc Thủ thư) */
  async createPhieuMuon(request: MuonSachRequest): Promise<any> {
    const token = localStorage.getItem('accessToken')
    const response = await fetch(`${BASE_URL}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(request)
    })
    return xuLyResponse<any>(response)
  },

  /** Lấy chi tiết phiếu mượn theo ID */
  async getById(id: string): Promise<any> {
    const token = localStorage.getItem('accessToken')
    const response = await fetch(`${BASE_URL}/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    return xuLyResponse<any>(response)
  },

  /** Lấy danh sách phiếu mượn của người dùng hiện tại */
  async getByNguoiDung(maNguoiDung: string, page = 0, size = 10): Promise<PageResponse<any>> {
    const token = localStorage.getItem('accessToken')
    const response = await fetch(`${BASE_URL}/nguoi-dung/${maNguoiDung}?page=${page}&size=${size}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    return xuLyResponse<PageResponse<any>>(response)
  }
}
