// /**
//  * datChoService.ts — Service quản lý Đặt chỗ mượn sách.
//  */
// import apiClient from './apiClient'
// import type { PageResponse } from '@/types/common'
// import type { DatCho, HuyDatChoRequest, TrangThaiDatCho } from '@/types/datcho'

// export const datChoService = {
//   danhSach: () =>
//     apiClient.get<DatCho[]>('/api/v1/dat-cho'),

//   /** Độc giả tự đặt chỗ mượn sách theo sachId */
//   taoChoDocGia: (sachId: number) =>
//     apiClient.post<DatCho>('/api/v1/dat-cho', { sachId }),

//   duyet: (id: number) =>
//     apiClient.put<DatCho>(`/api/v1/dat-cho/${id}/duyet`),

//   huy: (id: number, body: HuyDatChoRequest) =>
//     apiClient.put<DatCho>(`/api/v1/dat-cho/${id}/huy`, body)
// }
/**
 * datChoService.ts — (MOCK DATA) Service quản lý Đặt chỗ mượn sách.
 */
import type { DatCho, HuyDatChoRequest } from '@/types/datcho'

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

let mockDatCho: DatCho[] = [
  {
    maDatCho: 1,
    nguoiDung: { maNguoiDung: '3', hoDem: 'Lê Văn', ten: 'C', email: 'lvc@school.edu.vn' },
    sach: { maSach: 1, tenSach: 'Kính Vạn Hoa - Tập 1', isbn: '978-604-2-22874-5' },
    ngayDat: '2026-05-11T10:00:00Z',
    trangThai: 'CHO_DUYET'
  },
  {
    maDatCho: 2,
    nguoiDung: { maNguoiDung: '4', hoDem: 'Phạm Thị', ten: 'D', email: 'ptd@school.edu.vn' },
    sach: { maSach: 2, tenSach: 'Đắc Nhân Tâm', isbn: '978-604-3-18465-1' },
    ngayDat: '2026-05-09T14:30:00Z',
    trangThai: 'DA_SAN_SANG'
  }
]

export const datChoService = {
  danhSach: async () => { await delay(500); return [...mockDatCho] },

  taoChoDocGia: async (sachId: number) => {
    await delay(600)
    const newId = mockDatCho.length ? Math.max(...mockDatCho.map(d => d.maDatCho)) + 1 : 1
    const newItem: DatCho = {
      maDatCho: newId,
      nguoiDung: { maNguoiDung: '99', hoDem: 'Sinh viên', ten: 'Test', email: 'sv@school.edu.vn' },
      sach: { maSach: sachId, tenSach: 'Sách Mock', isbn: 'MOCK-ISBN' },
      ngayDat: new Date().toISOString(),
      trangThai: 'CHO_DUYET'
    }
    mockDatCho.push(newItem)
    return newItem
  },

  duyet: async (id: number) => {
    await delay(500)
    const index = mockDatCho.findIndex(d => d.maDatCho === id)
    if (index !== -1) {
      const datCho = mockDatCho[index]
      if (datCho) datCho.trangThai = 'DA_SAN_SANG'
    }
    return mockDatCho[index]
  },

  huy: async (id: number, body: HuyDatChoRequest) => {
    await delay(500)
    const index = mockDatCho.findIndex(d => d.maDatCho === id)
    if (index !== -1) {
      const datCho = mockDatCho[index]
      if (datCho) {
        datCho.trangThai = 'DA_HUY'
        console.log(`Đã hủy đặt chỗ. Lý do: ${body.lyDo}`) // In ra console để biết data truyền đúng
      }
    }
    return mockDatCho[index]
  }
}