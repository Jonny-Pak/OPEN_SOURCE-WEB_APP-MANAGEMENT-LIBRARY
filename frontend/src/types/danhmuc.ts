/**
 * danhmuc.ts — Kiểu dữ liệu cho Danh mục: Tác giả, Nhà xuất bản, Thể loại.
 */

export interface TacGia {
  maTacGia: number
  hoDem: string
  ten: string
  tieuSu?: string
  hinhAnh?: string
}

export interface TaoTacGiaRequest {
  hoDem: string
  ten: string
  tieuSu?: string
  hinhAnh?: string
}

export interface NhaXuatBan {
  maNhaXuatBan: number
  tenNhaXuatBan: string
  diaChi?: string
  soDienThoai?: string
  email?: string
}

export interface TaoNhaXuatBanRequest {
  tenNhaXuatBan: string
  diaChi?: string
  soDienThoai?: string
  email?: string
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
