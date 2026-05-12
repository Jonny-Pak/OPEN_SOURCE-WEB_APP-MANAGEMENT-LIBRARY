/**
 * danhmuc.ts — Kiểu dữ liệu cho Danh mục: Tác giả, Nhà xuất bản, Thể loại.
 */

export interface TacGia {
  maTacGia: number
  tenTacGia: string
  tieuSu?: string
}

export interface TaoTacGiaRequest {
  tenTacGia: string
  tieuSu?: string
}

export interface NhaXuatBan {
  maNXB: number
  tenNXB: string
  diaChi?: string
  website?: string
}

export interface TaoNhaXuatBanRequest {
  tenNXB: string
  diaChi?: string
  website?: string
}

export interface TheLoai {
  maTheLoai: number
  tenTheLoai: string
  moTa?: string
}

export interface TaoTheLoaiRequest {
  tenTheLoai: string
  moTa?: string
}
