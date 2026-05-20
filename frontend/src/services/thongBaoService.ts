/**
 * thongBaoService.ts — Service quản lý thông báo in-app.
 */
import apiClient from './apiClient'
import type { ThongBao } from '@/types/thongbao'

export const thongBaoService = {
  layDanhSach: (maNguoiDung: string): Promise<ThongBao[]> =>
    apiClient.get<ThongBao[]>(`/api/v1/thong-bao/nguoi-dung/${maNguoiDung}`),

  demChuaDoc: (maNguoiDung: string): Promise<number> =>
    apiClient.get<number>(`/api/v1/thong-bao/nguoi-dung/${maNguoiDung}/chua-doc/count`),

  danhDauDaDoc: (maThongBao: string): Promise<void> =>
    apiClient.put<void>(`/api/v1/thong-bao/${maThongBao}/da-doc`, {}),

  danhDauTatCaDaDoc: (maNguoiDung: string): Promise<void> =>
    apiClient.put<void>(`/api/v1/thong-bao/nguoi-dung/${maNguoiDung}/da-doc-het`, {}),
}

export default thongBaoService
