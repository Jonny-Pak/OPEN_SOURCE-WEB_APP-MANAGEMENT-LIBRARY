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
  danhSach: async (): Promise<LichSuGiaHan[]> => {
    const res: any = await apiClient.get<any[]>('/api/v1/phieu-muon/gia-han/danh-sach')
    if (Array.isArray(res)) {
      return res.map((item: any) => ({
        maGiaHan: item.maLichSuGiaHan,
        nguoiDung: {
          maNguoiDung: '',
          hoDem: item.tenDocGia || '',
          ten: ''
        },
        tenSach: item.tenSach || 'N/A',
        maBarcodeVatLy: item.maChiTietPhieuMuon ? String(item.maChiTietPhieuMuon).substring(0, 8).toUpperCase() : 'N/A',
        hanTraHienTai: item.hanTraCu || '',
        hanTraXinGiaHan: item.hanTraMoi || '',
        trangThai: item.trangThai || 'CHO_DUYET',
        lyDoTuChoi: item.lyDo || ''
      }))
    }
    return []
  },

  duyet: async (id: any, body: DuyetGiaHanRequest) => {
    return apiClient.post(`/api/v1/phieu-muon/gia-han/duyet/${id}?dongY=true`, {})
  },

  tuChoi: async (id: any, body: TuChoiGiaHanRequest) => {
    return apiClient.post(`/api/v1/phieu-muon/gia-han/duyet/${id}?dongY=false`, {})
  }
}