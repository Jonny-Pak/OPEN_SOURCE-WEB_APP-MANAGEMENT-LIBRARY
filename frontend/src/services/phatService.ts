// /**
//  * phatService.ts — Service quản lý Phiếu phạt.
//  */
// import apiClient from './apiClient'
// import type { PageResponse } from '@/types/common'
// import type { PhieuPhat, TaoPhieuPhatRequest, ThanhToanRequest, TrangThaiPhieuPhat } from '@/types/phat'

// export const phatService = {
//   danhSach: () =>
//     apiClient.get<PhieuPhat[]>('/api/v1/phieu-phat'),

//   taoCai: (body: TaoPhieuPhatRequest) =>
//     apiClient.post<PhieuPhat>('/api/v1/phieu-phat', body),

//   thanhToan: (id: number, body: ThanhToanRequest) =>
//     apiClient.put<PhieuPhat>(`/api/v1/phieu-phat/${id}/thanh-toan`, body),
// }
/**
 * phatService.ts — (MOCK DATA) Service quản lý Phiếu phạt.
 */
import type { PhieuPhat, TaoPhieuPhatRequest, ThanhToanRequest } from '@/types/phat'

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

let mockPhieuPhat: PhieuPhat[] = [
  {
    maPhieuPhat: 1,
    nguoiDung: { maNguoiDung: '1', hoDem: 'Nguyễn Văn', ten: 'A', email: 'nva@school.edu.vn' },
    maPhieuMuon: 101,
    lyDo: 'TRA_TRE',
    soTienPhat: 15000,
    ngayTao: '2026-05-10T14:00:00Z',
    trangThai: 'CHUA_THANH_TOAN'
  },
  {
    maPhieuPhat: 2,
    nguoiDung: { maNguoiDung: '3', hoDem: 'Lê Văn', ten: 'C', email: 'lvc@school.edu.vn' },
    maPhieuMuon: 102,
    lyDo: 'HU_HONG',
    soTienPhat: 50000,
    ngayTao: '2026-04-15T09:30:00Z',
    trangThai: 'DA_THANH_TOAN',
    phuongThucThanhToan: 'TIEN_MAT'
  }
]

export const phatService = {
  danhSach: async () => { await delay(500); return [...mockPhieuPhat] },

  taoCai: async (body: TaoPhieuPhatRequest) => {
    await delay(600)
    const newId = mockPhieuPhat.length ? Math.max(...mockPhieuPhat.map(p => p.maPhieuPhat)) + 1 : 1
    const newPhieu: PhieuPhat = {
      maPhieuPhat: newId,
      nguoiDung: { maNguoiDung: '99', hoDem: 'Độc giả', ten: 'Bị Phạt', email: 'phat@school.edu.vn' },
      maPhieuMuon: body.phieuMuonId,
      lyDo: body.lyDo,
      soTienPhat: body.soTienPhat,
      ngayTao: new Date().toISOString(),
      trangThai: 'CHUA_THANH_TOAN'
    }
    mockPhieuPhat.push(newPhieu)
    return newPhieu
  },

  thanhToan: async (id: number, body: ThanhToanRequest) => {
    await delay(600)
    const index = mockPhieuPhat.findIndex(p => p.maPhieuPhat === id)
    if (index !== -1) {
      const phieu = mockPhieuPhat[index]
      if (phieu) {
        phieu.trangThai = 'DA_THANH_TOAN'
        phieu.phuongThucThanhToan = body.phuongThucThanhToan
      }
    }
    return mockPhieuPhat[index]
  },
}