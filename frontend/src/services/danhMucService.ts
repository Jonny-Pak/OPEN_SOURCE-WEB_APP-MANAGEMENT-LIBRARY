/**
 * danhMucService.ts — Service quản lý Danh mục: Tác giả, Nhà xuất bản, Thể loại.
 */
import apiClient from './apiClient'
import type {
  TacGia, TaoTacGiaRequest,
  NhaXuatBan, TaoNhaXuatBanRequest,
  TheLoai, TaoTheLoaiRequest,
} from '@/types/danhmuc'

// ===== TÁC GIẢ =====
export const tacGiaService = {
  danhSach: () =>
    apiClient.get<TacGia[]>('/api/v1/tac-gia'),
  taoCai: (body: TaoTacGiaRequest) =>
    apiClient.post<TacGia>('/api/v1/tac-gia', body),
  capNhat: (id: number, body: TaoTacGiaRequest) =>
    apiClient.put<TacGia>(`/api/v1/tac-gia/${id}`, body),
  xoa: (id: number) =>
    apiClient.delete<void>(`/api/v1/tac-gia/${id}`),
}

// ===== NHÀ XUẤT BẢN =====
export const nhaXuatBanService = {
  danhSach: () =>
    apiClient.get<NhaXuatBan[]>('/api/v1/nha-xuat-ban'),
  taoCai: (body: TaoNhaXuatBanRequest) =>
    apiClient.post<NhaXuatBan>('/api/v1/nha-xuat-ban', body),
  capNhat: (id: number, body: TaoNhaXuatBanRequest) =>
    apiClient.put<NhaXuatBan>(`/api/v1/nha-xuat-ban/${id}`, body),
  xoa: (id: number) =>
    apiClient.delete<void>(`/api/v1/nha-xuat-ban/${id}`),
}

// ===== THỂ LOẠI =====
export const theLoaiService = {
  danhSach: () =>
    apiClient.get<TheLoai[]>('/api/v1/the-loai'),
  taoCai: (body: TaoTheLoaiRequest) =>
    apiClient.post<TheLoai>('/api/v1/the-loai', body),
  capNhat: (id: number, body: TaoTheLoaiRequest) =>
    apiClient.put<TheLoai>(`/api/v1/the-loai/${id}`, body),
  xoa: (id: number) =>
    apiClient.delete<void>(`/api/v1/the-loai/${id}`),
}