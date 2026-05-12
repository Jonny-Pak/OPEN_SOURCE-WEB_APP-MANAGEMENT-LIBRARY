// /**
//  * cuonSachService.ts — Service quản lý Cuốn sách (bản sao vật lý).
//  */
// import apiClient from './apiClient'
// import type { PageResponse } from '@/types/common'
// import type { CuonSach, TaoCuonSachRequest, SuaCuonSachRequest, TrangThaiCuonSach } from '@/types/sach'

// export const cuonSachService = {
//   danhSach: () =>
//     apiClient.get<CuonSach[]>('/api/v1/cuon-sach'),

//   taoCai: (body: TaoCuonSachRequest) =>
//     apiClient.post<CuonSach>('/api/v1/cuon-sach', body),

//   capNhat: (id: number, body: SuaCuonSachRequest) =>
//     apiClient.put<CuonSach>(`/api/v1/cuon-sach/${id}`, body),

//   xoa: (id: number) =>
//     apiClient.delete<void>(`/api/v1/cuon-sach/${id}`),

//   /** Lấy mã vạch SVG/PNG của cuốn sách */
//   layMaVach: (id: number): string =>
//     `http://localhost:8080/api/v1/cuon-sach/${id}/ma-vach`,
// }
/**
 * cuonSachService.ts — (MOCK DATA) Service quản lý Cuốn sách (bản sao vật lý).
 */
import type { CuonSach, TaoCuonSachRequest, SuaCuonSachRequest } from '@/types/sach'

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

let mockCuonSach: CuonSach[] = [
  {
    maCuonSach: 1,
    maBarcodeVatLy: 'KVH-001',
    viTriKe: 'Kệ A - Tầng 1',
    tinhTrangVatLy: 'TOT',
    trangThai: 'TRONG',
    sach: { maSach: 1, tenSach: 'Kính Vạn Hoa - Tập 1', isbn: '978-604-2-22874-5' }
  },
  {
    maCuonSach: 2,
    maBarcodeVatLy: 'KVH-002',
    viTriKe: 'Kệ A - Tầng 1',
    tinhTrangVatLy: 'HU_HONG',
    trangThai: 'BAO_TRI',
    sach: { maSach: 1, tenSach: 'Kính Vạn Hoa - Tập 1', isbn: '978-604-2-22874-5' }
  },
  {
    maCuonSach: 3,
    maBarcodeVatLy: 'HP-001',
    viTriKe: 'Kệ C - Tầng 2',
    tinhTrangVatLy: 'TOT',
    trangThai: 'DA_MUON',
    sach: { maSach: 2, tenSach: 'Harry Potter và Hòn Đá Phù Thủy', isbn: '978-604-1-15525-7' }
  }
]

export const cuonSachService = {
  danhSach: async () => { await delay(500); return [...mockCuonSach] },

  taoCai: async (body: TaoCuonSachRequest) => {
    await delay(600)
    const newId = mockCuonSach.length ? Math.max(...mockCuonSach.map(c => c.maCuonSach)) + 1 : 1
    const newItem: CuonSach = {
      maCuonSach: newId,
      maBarcodeVatLy: `MOCK-${newId}`,
      viTriKe: body.viTriKe,
      tinhTrangVatLy: body.tinhTrangVatLy,
      trangThai: 'TRONG',
      sach: { maSach: body.sachId, tenSach: 'Sách Mock', isbn: 'MOCK-ISBN' }
    }
    mockCuonSach.push(newItem)
    return newItem
  },

  capNhat: async (id: number, body: SuaCuonSachRequest) => {
    await delay(500)
    const index = mockCuonSach.findIndex(c => c.maCuonSach === id)

    if (index !== -1) {
      const cuonSachToUpdate = mockCuonSach[index]
      if (cuonSachToUpdate) {
        cuonSachToUpdate.viTriKe = body.viTriKe
        cuonSachToUpdate.tinhTrangVatLy = body.tinhTrangVatLy
      }
    }
    return mockCuonSach[index]
  },

  xoa: async (id: number) => {
    await delay(400)
    mockCuonSach = mockCuonSach.filter(c => c.maCuonSach !== id)
  },

  layMaVach: (id: number): string => {
    // Trả về một link ảnh mã vạch ngẫu nhiên dùng để test UI
    return `https://barcode.tec-it.com/barcode.ashx?data=MOCK-${id}&code=Code128&dpi=96`
  },
}