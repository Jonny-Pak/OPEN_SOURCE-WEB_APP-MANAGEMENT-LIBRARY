// /**
//  * muonSachService.ts — Service quản lý Mượn sách.
//  */
// import apiClient from './apiClient'
// import type { PageResponse } from '@/types/common'
// import type {
//   PhieuMuon, TaoPhieuMuonRequest, ThemChiTietRequest,
//   TrangThaiPhieuMuon,
// } from '@/types/muonsach'

// export const muonSachService = {
//   danhSach: () =>
//     apiClient.get<PhieuMuon[]>('/api/v1/phieu-muon'),

//   layChiTiet: (id: number) =>
//     apiClient.get<PhieuMuon>(`/api/v1/phieu-muon/${id}`),

//   /** Bước 1: Tạo phiếu mượn (chưa có chi tiết sách) */
//   taoCai: (body: TaoPhieuMuonRequest) =>
//     apiClient.post<PhieuMuon>('/api/v1/phieu-muon', body),

//   /** Bước 2: Thêm sách vào phiếu mượn qua quét mã vạch */
//   themChiTiet: (phieuMuonId: number, body: ThemChiTietRequest) =>
//     apiClient.post<PhieuMuon>(`/api/v1/phieu-muon/${phieuMuonId}/chi-tiet`, body),
// }

/**
 * muonSachService.ts — (MOCK DATA) Service quản lý Mượn sách.
 */
import type {
  PhieuMuon, TaoPhieuMuonRequest, ThemChiTietRequest, ChiTietPhieuMuon
} from '@/types/muonsach'

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

let mockPhieuMuon: PhieuMuon[] = [
  {
    maPhieuMuon: 1,
    nguoiDung: { maNguoiDung: '1', hoDem: 'Nguyễn Văn', ten: 'A', email: 'nva@school.edu.vn' },
    ngayMuon: '2026-05-10T08:00:00Z',
    hanTra: '2026-05-17T08:00:00Z',
    trangThai: 'DANG_MUON',
    soLuongCuon: 2,
    chiTietList: [
      { maChiTiet: 1, maBarcodeVatLy: 'KVH-001', tenSach: 'Kính Vạn Hoa - Tập 1', hanTra: '2026-05-17T08:00:00Z', quaHan: false },
      { maChiTiet: 2, maBarcodeVatLy: 'HP-001', tenSach: 'Harry Potter', hanTra: '2026-05-17T08:00:00Z', quaHan: false }
    ]
  },
  {
    maPhieuMuon: 2,
    nguoiDung: { maNguoiDung: '2', hoDem: 'Trần Thị', ten: 'B', email: 'ttb@school.edu.vn' },
    ngayMuon: '2026-04-20T08:00:00Z',
    hanTra: '2026-04-27T08:00:00Z',
    trangThai: 'QUA_HAN',
    soLuongCuon: 1,
    chiTietList: [
      { maChiTiet: 3, maBarcodeVatLy: 'CLEAN-001', tenSach: 'Clean Code', hanTra: '2026-04-27T08:00:00Z', quaHan: true }
    ]
  }
]

export const muonSachService = {
  danhSach: async () => { await delay(500); return [...mockPhieuMuon] },

  layChiTiet: async (id: number) => {
    await delay(300)
    const phieu = mockPhieuMuon.find(p => p.maPhieuMuon === id)
    if (!phieu) throw new Error('Không tìm thấy phiếu mượn')
    return phieu
  },

  taoCai: async (body: TaoPhieuMuonRequest) => {
    await delay(600)
    const newId = mockPhieuMuon.length ? Math.max(...mockPhieuMuon.map(p => p.maPhieuMuon)) + 1 : 1
    const newPhieu: PhieuMuon = {
      maPhieuMuon: newId,
      // Mock data người dùng mặc định
      nguoiDung: { maNguoiDung: body.nguoiDungId || '99', hoDem: 'Độc giả', ten: 'Mock', email: 'mock@school.edu.vn' },
      ngayMuon: new Date().toISOString(),
      hanTra: body.hanTra,
      trangThai: 'DANG_MUON',
      soLuongCuon: 0,
      chiTietList: []
    }
    mockPhieuMuon.push(newPhieu)
    return newPhieu
  },

  themChiTiet: async (phieuMuonId: number, body: ThemChiTietRequest) => {
    await delay(600)
    const index = mockPhieuMuon.findIndex(p => p.maPhieuMuon === phieuMuonId)
    if (index !== -1) {
      const phieu = mockPhieuMuon[index]
      if (phieu) {
        // Sinh chi tiết giả lập từ danh sách barcode quét được
        const newDetails: ChiTietPhieuMuon[] = body.maBarcodeList.map((barcode, i) => ({
          maChiTiet: Date.now() + i,
          maBarcodeVatLy: barcode,
          tenSach: 'Sách quét từ mã ' + barcode,
          hanTra: phieu.hanTra,
          quaHan: false
        }))
        phieu.chiTietList.push(...newDetails)
        phieu.soLuongCuon = phieu.chiTietList.length
      }
    }
    return mockPhieuMuon[index]
  },
}