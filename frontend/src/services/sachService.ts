/**
 * sachService.ts — Service quản lý Đầu sách.
 */
import apiClient from './apiClient'
import type { Sach, TaoSachRequest } from '@/types/sach'

export const sachService = {
  danhSach: () =>
    apiClient.get<Sach[]>('/api/v1/sach'),

  layMotCuon: (id: number) =>
    apiClient.get<Sach>(`/api/v1/sach/${id}`),

  taoCai: (body: TaoSachRequest) =>
    apiClient.post<Sach>('/api/v1/sach', body),

  capNhat: (id: number, body: TaoSachRequest) =>
    apiClient.put<Sach>(`/api/v1/sach/${id}`, body),

  xoa: (id: number) =>
    apiClient.delete<void>(`/api/v1/sach/${id}`),

  /** Upload ảnh bìa sách */
  uploadAnhBia: (id: number, file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.upload<{ anhBiaUrl: string }>(`/api/v1/sach/${id}/anh-bia`, formData)
  },

  /** Xóa ảnh bìa sách */
  xoaAnhBia: (id: number) =>
    apiClient.delete<void>(`/api/v1/sach/${id}/anh-bia`),
}