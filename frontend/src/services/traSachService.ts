// /**
//  * traSachService.ts — Service Trả sách và Gia hạn.
//  */
// import apiClient from './apiClient'
// import type { PageResponse } from '@/types/common'
// import type { TraSachRequest } from '@/types/muonsach'
// import type { LichSuGiaHan, DuyetGiaHanRequest, TuChoiGiaHanRequest, TrangThaiGiaHan } from '@/types/giahan'

// export const traSachService = {
//   /** Xác nhận trả sách và tình trạng từng cuốn */
//   traSach: (phieuMuonId: number, body: TraSachRequest) =>
//     apiClient.put<void>(`/api/v1/phieu-muon/${phieuMuonId}/tra`, body),
// }

// export const giaHanService = {
//   danhSach: () =>
//     apiClient.get<LichSuGiaHan[]>('/api/v1/gia-han'),

//   duyet: (id: number, body: DuyetGiaHanRequest) =>
//     apiClient.put<LichSuGiaHan>(`/api/v1/gia-han/${id}/duyet`, body),

//   tuChoi: (id: number, body: TuChoiGiaHanRequest) =>
//     apiClient.put<LichSuGiaHan>(`/api/v1/gia-han/${id}/tu-choi`, body)
// }
/**
 * traSachService.ts — (MOCK DATA) Service Trả sách và Gia hạn.
 */
import type { TraSachRequest } from '@/types/muonsach'
import type { LichSuGiaHan, DuyetGiaHanRequest, TuChoiGiaHanRequest } from '@/types/giahan'

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

let mockGiaHan: LichSuGiaHan[] = [
  {
    maGiaHan: 1,
    nguoiDung: { maNguoiDung: '1', hoDem: 'Nguyễn Văn', ten: 'A' },
    tenSach: 'Kính Vạn Hoa - Tập 1',
    maBarcodeVatLy: 'KVH-001',
    hanTraHienTai: '2026-05-17T08:00:00Z',
    hanTraXinGiaHan: '2026-05-24T08:00:00Z',
    trangThai: 'CHO_DUYET'
  },
  {
    maGiaHan: 2,
    nguoiDung: { maNguoiDung: '2', hoDem: 'Trần Thị', ten: 'B' },
    tenSach: 'Clean Code',
    maBarcodeVatLy: 'CLEAN-001',
    hanTraHienTai: '2026-04-27T08:00:00Z',
    hanTraXinGiaHan: '2026-05-04T08:00:00Z',
    trangThai: 'TU_CHOI',
    lyDoTuChoi: 'Sách đang có nhiều người đặt hàng chờ mượn'
  }
]

export const traSachService = {
  traSach: async (phieuMuonId: number, body: TraSachRequest) => {
    await delay(800)
    // Giả lập logic trả sách thành công
    console.log(`Đã xử lý trả sách cho phiếu ${phieuMuonId}`, body.chiTietList)
    return Promise.resolve()
  },
}

export const giaHanService = {
  danhSach: async () => { await delay(500); return [...mockGiaHan] },

  duyet: async (id: number, body: DuyetGiaHanRequest) => {
    await delay(500)
    const index = mockGiaHan.findIndex(g => g.maGiaHan === id)
    if (index !== -1) {
      const giaHan = mockGiaHan[index]
      if (giaHan) {
        giaHan.trangThai = 'DA_DUYET'
        // Cập nhật mốc thời gian để UI thay đổi
        giaHan.hanTraHienTai = body.hanTraMoi
      }
    }
    return mockGiaHan[index]
  },

  tuChoi: async (id: number, body: TuChoiGiaHanRequest) => {
    await delay(500)
    const index = mockGiaHan.findIndex(g => g.maGiaHan === id)
    if (index !== -1) {
      const giaHan = mockGiaHan[index]
      if (giaHan) {
        giaHan.trangThai = 'TU_CHOI'
        giaHan.lyDoTuChoi = body.lyDo
      }
    }
    return mockGiaHan[index]
  }
}