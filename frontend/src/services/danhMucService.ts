// /**
//  * danhMucService.ts — Service quản lý Danh mục: Tác giả, Nhà xuất bản, Thể loại.
//  */
// import apiClient from './apiClient'
// import type { PageResponse } from '@/types/common'
// import type {
//   TacGia, TaoTacGiaRequest,
//   NhaXuatBan, TaoNhaXuatBanRequest,
//   TheLoai, TaoTheLoaiRequest,
// } from '@/types/danhmuc'

// // ===== TÁC GIẢ =====
// export const tacGiaService = {
//   danhSach: () =>
//     apiClient.get<TacGia[]>('/api/v1/tac-gia'),
//   taoCai: (body: TaoTacGiaRequest) =>
//     apiClient.post<TacGia>('/api/v1/tac-gia', body),
//   capNhat: (id: number, body: TaoTacGiaRequest) =>
//     apiClient.put<TacGia>(`/api/v1/tac-gia/${id}`, body),
//   xoa: (id: number) =>
//     apiClient.delete<void>(`/api/v1/tac-gia/${id}`),
// }

// // ===== NHÀ XUẤT BẢN =====
// export const nhaXuatBanService = {
//   danhSach: () =>
//     apiClient.get<NhaXuatBan[]>('/api/v1/nha-xuat-ban'),
//   taoCai: (body: TaoNhaXuatBanRequest) =>
//     apiClient.post<NhaXuatBan>('/api/v1/nha-xuat-ban', body),
//   capNhat: (id: number, body: TaoNhaXuatBanRequest) =>
//     apiClient.put<NhaXuatBan>(`/api/v1/nha-xuat-ban/${id}`, body),
//   xoa: (id: number) =>
//     apiClient.delete<void>(`/api/v1/nha-xuat-ban/${id}`),
// }

// // ===== THỂ LOẠI =====
// export const theLoaiService = {
//   danhSach: () =>
//     apiClient.get<TheLoai[]>('/api/v1/the-loai'),
//   taoCai: (body: TaoTheLoaiRequest) =>
//     apiClient.post<TheLoai>('/api/v1/the-loai', body),
//   capNhat: (id: number, body: TaoTheLoaiRequest) =>
//     apiClient.put<TheLoai>(`/api/v1/the-loai/${id}`, body),
//   xoa: (id: number) =>
//     apiClient.delete<void>(`/api/v1/the-loai/${id}`),
// }
/**
 * danhMucService.ts — (MOCK DATA) Service quản lý Danh mục: Tác giả, NXB, Thể loại.
 */
import type {
  TacGia, TaoTacGiaRequest,
  NhaXuatBan, TaoNhaXuatBanRequest,
  TheLoai, TaoTheLoaiRequest,
} from '@/types/danhmuc'

// Hàm giả lập độ trễ mạng
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

// DỮ LIỆU TẠM THỜI TRÊN RAM
let mockTacGia: TacGia[] = [
  { maTacGia: 1, tenTacGia: 'Nguyễn Nhật Ánh', tieuSu: 'Nhà văn nổi tiếng Việt Nam' },
  { maTacGia: 2, tenTacGia: 'J.K. Rowling', tieuSu: 'Tác giả Harry Potter' }
]

let mockNXB: NhaXuatBan[] = [
  { maNXB: 1, tenNXB: 'NXB Trẻ', diaChi: 'TP.HCM', website: 'nxbtre.com.vn' },
  { maNXB: 2, tenNXB: 'Kim Đồng', diaChi: 'Hà Nội', website: 'nxbkimdong.com.vn' }
]

let mockTheLoai: TheLoai[] = [
  { maTheLoai: 1, tenTheLoai: 'Văn học trong nước', moTa: 'Truyện dài, tiểu thuyết VN' },
  { maTheLoai: 2, tenTheLoai: 'Giáo khoa - Giáo trình', moTa: 'Sách phục vụ học tập' }
]

// ===== TÁC GIẢ =====
export const tacGiaService = {
  danhSach: async () => { await delay(400); return [...mockTacGia] },
  taoCai: async (body: TaoTacGiaRequest) => {
    await delay(500)
    const newId = mockTacGia.length ? Math.max(...mockTacGia.map(t => t.maTacGia)) + 1 : 1
    const newItem: TacGia = { maTacGia: newId, ...body }
    mockTacGia.push(newItem)
    return newItem
  },
  capNhat: async (id: number, body: TaoTacGiaRequest) => {
    await delay(500)
    const index = mockTacGia.findIndex(t => t.maTacGia === id)
    if (index !== -1) mockTacGia[index] = { ...mockTacGia[index], ...body } as TacGia
    return mockTacGia[index]
  },
  xoa: async (id: number) => {
    await delay(400)
    mockTacGia = mockTacGia.filter(t => t.maTacGia !== id)
  },
}

// ===== NHÀ XUẤT BẢN =====
export const nhaXuatBanService = {
  danhSach: async () => { await delay(400); return [...mockNXB] },
  taoCai: async (body: TaoNhaXuatBanRequest) => {
    await delay(500)
    const newId = mockNXB.length ? Math.max(...mockNXB.map(n => n.maNXB)) + 1 : 1
    const newItem: NhaXuatBan = { maNXB: newId, ...body }
    mockNXB.push(newItem)
    return newItem
  },
  capNhat: async (id: number, body: TaoNhaXuatBanRequest) => {
    await delay(500)
    const index = mockNXB.findIndex(n => n.maNXB === id)
    if (index !== -1) mockNXB[index] = { ...mockNXB[index], ...body } as NhaXuatBan
    return mockNXB[index]
  },
  xoa: async (id: number) => {
    await delay(400)
    mockNXB = mockNXB.filter(n => n.maNXB !== id)
  },
}

// ===== THỂ LOẠI =====
export const theLoaiService = {
  danhSach: async () => { await delay(400); return [...mockTheLoai] },
  taoCai: async (body: TaoTheLoaiRequest) => {
    await delay(500)
    const newId = mockTheLoai.length ? Math.max(...mockTheLoai.map(t => t.maTheLoai)) + 1 : 1
    const newItem: TheLoai = { maTheLoai: newId, ...body }
    mockTheLoai.push(newItem)
    return newItem
  },
  capNhat: async (id: number, body: TaoTheLoaiRequest) => {
    await delay(500)
    const index = mockTheLoai.findIndex(t => t.maTheLoai === id)
    if (index !== -1) mockTheLoai[index] = { ...mockTheLoai[index], ...body } as TheLoai
    return mockTheLoai[index]
  },
  xoa: async (id: number) => {
    await delay(400)
    mockTheLoai = mockTheLoai.filter(t => t.maTheLoai !== id)
  },
}