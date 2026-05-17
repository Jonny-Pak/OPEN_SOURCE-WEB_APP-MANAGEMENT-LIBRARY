/**
 * traSachService.ts — Service Trả sách và Gia hạn.
 */
import apiClient from './apiClient'
import type { PageResponse } from '@/types/common'
import type { TraSachRequest, TraCuonSachRequest, TraToanBoRequest } from '@/types/muonsach'
import type { LichSuGiaHan, DuyetGiaHanRequest, TuChoiGiaHanRequest } from '@/types/giahan'

const mapTinhTrang = (t: string): string => {
  if (t === 'TOT') return 'BINH_THUONG'
  if (t === 'HU_HONG') return 'RACH_TRANG'
  if (t === 'MAT') return 'HONG_NANG'
  return 'BINH_THUONG'
}

export const traSachService = {
  /** Xác nhận trả toàn bộ sách trong phiếu */
  traToanBo: (body: TraToanBoRequest) =>
    apiClient.post<void>('/api/v1/phieu-muon/tra-toan-bo', {
      maPhieuMuon: body.maPhieuMuon,
      tinhTrangLucTra: 'BINH_THUONG'
    }),

  /** Xác nhận trả 1 cuốn sách (kèm tình trạng) */
  traCuonSach: (body: TraCuonSachRequest) =>
    apiClient.post<void>('/api/v1/phieu-muon/tra-sach', {
      maChiTietPhieuMuon: body.maChiTietPhieuMuon,
      tinhTrangLucTra: mapTinhTrang(body.tinhTrangVatLyKhiTra)
    }),

  /** Báo mất sách */
  baoMatSach: (maChiTietPhieuMuon: string) =>
    apiClient.post<void>(`/api/v1/phieu-muon/mat-sach/${maChiTietPhieuMuon}`),
}

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

const mockGiaHan: LichSuGiaHan[] = [
  {
    maGiaHan: 1,
    nguoiDung: { maNguoiDung: '1', hoDem: 'Nguyễn Văn', ten: 'A' },
    tenSach: 'Kính Vạn Hoa - Tập 1',
    maBarcodeVatLy: 'KVH-001',
    hanTraHienTai: '2026-05-17T08:00:00Z',
    hanTraXinGiaHan: '2026-05-24T08:00:00Z',
    trangThai: 'CHO_DUYET'
  }
]

export const giaHanService = {
  danhSach: async () => { await delay(500); return [...mockGiaHan] },

  duyet: async (id: number, body: DuyetGiaHanRequest) => {
    await delay(500)
    return mockGiaHan[0]
  },

  tuChoi: async (id: number, body: TuChoiGiaHanRequest) => {
    await delay(500)
    return mockGiaHan[0]
  }
}