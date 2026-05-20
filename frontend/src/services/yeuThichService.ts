/**
 * yeuThichService.ts — Service gọi API quản lý sách yêu thích.
 */
import type { Sach } from '@/types/sach'

const BASE_URL = 'http://localhost:8080/api/v1/sach-yeu-thich'

export interface SachYeuThichResponse {
  maSachYeuThich: number
  sach: Sach
}

async function xuLyResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json().catch(() => ({}))
    throw error
  }
  if (response.status === 204) return {} as T
  return await response.json().catch(() => ({}))
}

export const yeuThichService = {
  /** Lấy danh sách sách yêu thích của người dùng */
  async getDanhSach(maNguoiDung: string): Promise<SachYeuThichResponse[]> {
    const token = localStorage.getItem('accessToken')
    const response = await fetch(`${BASE_URL}/nguoi-dung/${maNguoiDung}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    return xuLyResponse<SachYeuThichResponse[]>(response)
  },

  /** Thêm sách vào danh sách yêu thích */
  async them(maNguoiDung: string, maSach: number): Promise<void> {
    const token = localStorage.getItem('accessToken')
    const response = await fetch(`${BASE_URL}/nguoi-dung/${maNguoiDung}/sach/${maSach}`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    return xuLyResponse<void>(response)
  },

  /** Xóa sách khỏi danh sách yêu thích */
  async xoa(maNguoiDung: string, maSach: number): Promise<void> {
    const token = localStorage.getItem('accessToken')
    const response = await fetch(`${BASE_URL}/nguoi-dung/${maNguoiDung}/sach/${maSach}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    return xuLyResponse<void>(response)
  }
}
