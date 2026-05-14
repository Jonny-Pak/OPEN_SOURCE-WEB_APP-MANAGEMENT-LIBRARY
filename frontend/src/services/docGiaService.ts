// /**
//  * docGiaService.ts — Service quản lý Độc giả / Người dùng.
//  */
// import apiClient from './apiClient'
// import type { NguoiDung, TaoNguoiDungExcelRequest, TaoNguoiDungRequest, SuaNguoiDungRequest } from '@/types/nguoidung'

// export const docGiaService = {
//   danhSach: () =>
//     apiClient.get<NguoiDung[]>('/api/v1/nguoi-dung'),

//   taoCai: (body: TaoNguoiDungRequest) =>
//     apiClient.post<NguoiDung>('/api/v1/nguoi-dung', body),

//   capNhat: (id: string, body: SuaNguoiDungRequest) =>
//     apiClient.put<NguoiDung>(`/api/v1/nguoi-dung/${id}`, body),

//   xoa: (id: string) =>
//     apiClient.delete<void>(`/api/v1/admin/nguoi-dung/${id}`),

//   khoa: (id: string) =>
//     apiClient.put<NguoiDung>(`/api/v1/admin/nguoi-dung/${id}/khoa`),

//   moKhoa: (id: string) =>
//     apiClient.put<NguoiDung>(`/api/v1/admin/nguoi-dung/${id}/mo-khoa`),

//   kichHoat: (id: string) =>
//     apiClient.put<NguoiDung>(`/api/v1/admin/nguoi-dung/${id}/kich-hoat`),

//   taoHangLoat: (danhSach: TaoNguoiDungExcelRequest[]) =>
//     apiClient.post<{ thanhCong: number; thatBai: number; loi: Array<{ email: string; message: string }> }>(
//       '/api/v1/admin/nguoi-dung/tao-hang-loat',
//       { danhSach },
//     ),
// }
/**
 * docGiaService.ts — (MOCK DATA) Service quản lý Độc giả / Người dùng.
 */
import type { NguoiDung, TaoNguoiDungExcelRequest, TaoNguoiDungRequest, SuaNguoiDungRequest } from '@/types/nguoidung'

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

let mockNguoiDung: NguoiDung[] = [
  {
    maNguoiDung: '1',
    hoDem: 'Nguyễn Văn',
    ten: 'A',
    email: 'nva@school.edu.vn',
    soDienThoai: '0901234567',
    vaiTro: 'DOC_GIA',
    trangThai: 'HOAT_DONG',
    ngayTao: '2025-09-05T08:00:00Z'
  },
  {
    maNguoiDung: '2',
    hoDem: 'Trần Thị',
    ten: 'B',
    email: 'ttb@school.edu.vn',
    soDienThoai: '0912345678',
    vaiTro: 'DOC_GIA',
    trangThai: 'CHUA_KICH_HOAT',
    ngayTao: '2026-05-10T08:00:00Z'
  },
  {
    maNguoiDung: '3',
    hoDem: 'Lê Văn',
    ten: 'C',
    email: 'lvc@school.edu.vn',
    soDienThoai: '0923456789',
    vaiTro: 'DOC_GIA',
    trangThai: 'BI_KHOA',
    ngayTao: '2025-12-01T08:00:00Z'
  }
]

export const docGiaService = {
  danhSach: async () => { await delay(500); return [...mockNguoiDung] },

  taoCai: async (body: TaoNguoiDungRequest) => {
    await delay(600)
    const newId = `SV${Date.now().toString().slice(-5)}`
    const newDocGia: NguoiDung = {
      maNguoiDung: newId,
      hoDem: body.hoDem,
      ten: body.ten,
      email: body.email,
      soDienThoai: body.soDienThoai,
      vaiTro: body.vaiTro || 'DOC_GIA',
      trangThai: body.trangThai || 'CHUA_KICH_HOAT',
      ngayTao: new Date().toISOString()
    }
    mockNguoiDung.push(newDocGia)
    return newDocGia
  },

  capNhat: async (id: string, body: SuaNguoiDungRequest) => {
    await delay(500)
    const index = mockNguoiDung.findIndex(nd => nd.maNguoiDung === id)
    if (index !== -1) {
      const nguoiDungToUpdate = mockNguoiDung[index]
      if (nguoiDungToUpdate) {
        nguoiDungToUpdate.hoDem = body.hoDem
        nguoiDungToUpdate.ten = body.ten
        nguoiDungToUpdate.soDienThoai = body.soDienThoai
      }
    }
    return mockNguoiDung[index]
  },

  xoa: async (id: string) => {
    await delay(400)
    mockNguoiDung = mockNguoiDung.filter(nd => nd.maNguoiDung !== id)
  },

  khoa: async (id: string) => {
    await delay(400)
    const index = mockNguoiDung.findIndex(nd => nd.maNguoiDung === id)
    if (index !== -1) {
      const nguoiDung = mockNguoiDung[index]
      if (nguoiDung) nguoiDung.trangThai = 'BI_KHOA'
    }
    return mockNguoiDung[index]
  },

  moKhoa: async (id: string) => {
    await delay(400)
    const index = mockNguoiDung.findIndex(nd => nd.maNguoiDung === id)
    if (index !== -1) {
      const nguoiDung = mockNguoiDung[index]
      if (nguoiDung) nguoiDung.trangThai = 'HOAT_DONG'
    }
    return mockNguoiDung[index]
  },

  kichHoat: async (id: string) => {
    await delay(400)
    const index = mockNguoiDung.findIndex(nd => nd.maNguoiDung === id)
    if (index !== -1) {
      const nguoiDung = mockNguoiDung[index]
      if (nguoiDung) nguoiDung.trangThai = 'HOAT_DONG' // Chuyển thẳng sang Hoạt động
    }
    return mockNguoiDung[index]
  },

  taoHangLoat: async (danhSach: TaoNguoiDungExcelRequest[]) => {
    await delay(1000) // Giả lập load file nặng xíu
    const soLuong = danhSach.length
    // Chèn luôn vào mảng mock
    danhSach.forEach(item => {
      mockNguoiDung.push({
        maNguoiDung: item.mssv || `SV${Date.now().toString().slice(-4)}`,
        hoDem: item.hoDem,
        ten: item.ten,
        email: item.email,
        soDienThoai: '0123456789', // Fake default
        vaiTro: 'DOC_GIA',
        trangThai: 'CHUA_KICH_HOAT',
        ngayTao: new Date().toISOString()
      })
    })
    return { thanhCong: soLuong, thatBai: 0, loi: [] }
  },
}