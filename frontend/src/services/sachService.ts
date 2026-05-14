// /**
//  * sachService.ts — Service quản lý Đầu sách.
//  */
// import apiClient from './apiClient'
// import type { PageResponse } from '@/types/common'
// import type { Sach, TaoSachRequest } from '@/types/sach'

// export const sachService = {
//   danhSach: () =>
//     apiClient.get<Sach[]>('/api/v1/sach'),

//   layMotCuon: (id: number) =>
//     apiClient.get<Sach>(`/api/v1/sach/${id}`),

//   taoCai: (body: TaoSachRequest) =>
//     apiClient.post<Sach>('/api/v1/sach', body),

//   capNhat: (id: number, body: TaoSachRequest) =>
//     apiClient.put<Sach>(`/api/v1/sach/${id}`, body),

//   xoa: (id: number) =>
//     apiClient.delete<void>(`/api/v1/sach/${id}`),

//   /** Upload ảnh bìa sách */
//   uploadAnhBia: (id: number, file: File) => {
//     const formData = new FormData()
//     formData.append('file', file)
//     return apiClient.upload<{ anhBiaUrl: string }>(`/api/v1/sach/${id}/anh-bia`, formData)
//   },

//   /** Xóa ảnh bìa sách */
//   xoaAnhBia: (id: number) =>
//     apiClient.delete<void>(`/api/v1/sach/${id}/anh-bia`),
// }

/**
 * sachService.ts — (MOCK DATA) Service quản lý Đầu sách.
 */
import type { Sach, TaoSachRequest } from '@/types/sach'

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

let mockSach: Sach[] = [
  {
    maSach: 1,
    tenSach: 'Kính Vạn Hoa - Tập 1',
    isbn: '978-604-2-22874-5',
    namXuatBan: 2021,
    moTa: 'Truyện thiếu nhi kinh điển',
    anhBiaUrl: '',
    nhaXuatBan: { maNXB: 2, tenNXB: 'Kim Đồng' },
    tacGias: [{ maTacGia: 1, tenTacGia: 'Nguyễn Nhật Ánh' }],
    theLoais: [{ maTheLoai: 1, tenTheLoai: 'Văn học trong nước' }]
  },
  {
    maSach: 2,
    tenSach: 'Harry Potter và Hòn Đá Phù Thủy',
    isbn: '978-604-1-15525-7',
    namXuatBan: 2020,
    moTa: 'Bản dịch tiếng Việt',
    anhBiaUrl: '',
    nhaXuatBan: { maNXB: 1, tenNXB: 'NXB Trẻ' },
    tacGias: [{ maTacGia: 2, tenTacGia: 'J.K. Rowling' }],
    theLoais: [{ maTheLoai: 1, tenTheLoai: 'Văn học nước ngoài' }]
  }
]

export const sachService = {
  danhSach: async () => { await delay(500); return [...mockSach] },

  layMotCuon: async (id: number) => {
    await delay(300)
    const sach = mockSach.find(s => s.maSach === id)
    if (!sach) throw new Error('Không tìm thấy sách')
    return sach
  },

  taoCai: async (body: TaoSachRequest) => {
    await delay(600)
    const newId = mockSach.length ? Math.max(...mockSach.map(s => s.maSach)) + 1 : 1
    const newSach: Sach = {
      maSach: newId,
      tenSach: body.tenSach,
      isbn: body.isbn,
      namXuatBan: body.namXuatBan,
      moTa: body.moTa,
      anhBiaUrl: '',
      // Dùng dữ liệu giả định vì payload chỉ gửi Ids
      nhaXuatBan: { maNXB: body.nhaXuatBanId, tenNXB: 'NXB Mock' },
      tacGias: body.tacGiaIds.map(id => ({ maTacGia: id, tenTacGia: 'Tác giả Mock' })),
      theLoais: body.theLoaiIds.map(id => ({ maTheLoai: id, tenTheLoai: 'Thể loại Mock' }))
    }
    mockSach.push(newSach)
    return newSach
  },

  capNhat: async (id: number, body: TaoSachRequest) => {
    await delay(600)
    const index = mockSach.findIndex(s => s.maSach === id)

    if (index !== -1) {
      const sachToUpdate = mockSach[index]
      if (sachToUpdate) {
        sachToUpdate.tenSach = body.tenSach
        sachToUpdate.isbn = body.isbn
        sachToUpdate.namXuatBan = body.namXuatBan
        sachToUpdate.moTa = body.moTa
      }
    }
    return mockSach[index]
  },

  xoa: async (id: number) => {
    await delay(400)
    mockSach = mockSach.filter(s => s.maSach !== id)
  },

  uploadAnhBia: async (id: number, file: File) => {
    await delay(800)
    // Trả về một ảnh placeholder làm mock
    return { anhBiaUrl: 'https://via.placeholder.com/150' }
  },

  xoaAnhBia: async (id: number) => {
    await delay(400)
    const index = mockSach.findIndex(s => s.maSach === id)

    if (index !== -1) {
      const sachToUpdate = mockSach[index]
      if (sachToUpdate) {
        sachToUpdate.anhBiaUrl = ''
      }
    }
  },
}