/**
 * muonSachService.ts — Service quản lý Mượn sách.
 */
import apiClient from './apiClient'
import type {
  PhieuMuon, TaoPhieuMuonRequest, ThemChiTietRequest
} from '@/types/muonsach'

export const muonSachService = {
  /** Lấy danh sách phiếu mượn (Dành cho admin) */
  danhSach: () =>
    apiClient.get<PhieuMuon[]>('/api/v1/phieu-muon'),

  /** Lấy lịch sử mượn của một người dùng cụ thể */
  getLichSuMuon: (maNguoiDung: string) =>
    apiClient.get<PhieuMuon[]>(`/api/v1/phieu-muon/nguoi-dung/${maNguoiDung}`),

  /** Lấy chi tiết một phiếu mượn */
  layChiTiet: (id: string) =>
    apiClient.get<PhieuMuon>(`/api/v1/phieu-muon/${id}`),

  /** Bước 1: Tạo phiếu mượn (Dành cho admin/thủ thư) */
  taoCai: (body: TaoPhieuMuonRequest) =>
    apiClient.post<PhieuMuon>('/api/v1/phieu-muon', body),

  /** Bước 2: Thêm sách vào phiếu mượn (Dành cho admin/thủ thư) */
  themChiTiet: (phieuMuonId: string, body: ThemChiTietRequest) =>
    apiClient.post<PhieuMuon>(`/api/v1/phieu-muon/${phieuMuonId}/chi-tiet`, body),
}

export default muonSachService