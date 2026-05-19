/**
 * nguoidung.ts — Kiểu dữ liệu cho Người dùng / Độc giả.
 */

export type VaiTro = 'QUAN_TRI_VIEN' | 'THU_THU' | 'DOC_GIA'
export type TrangThaiNguoiDung =
  | 'HOAT_DONG'
  | 'KHOA'
  | 'chua_kich_hoat'
  | 'da_kich_hoat'
  | 'bi_khoa'
  | 'CHUA_KICH_HOAT'
  | 'DA_KICH_HOAT'
  | 'BI_KHOA'

export interface NguoiDung {
  maNguoiDung: string
  hoDem: string
  ten: string
  email: string
  soDienThoai: string
  vaiTro: VaiTro
  trangThai: TrangThaiNguoiDung
  ngayTao?: string
}

export interface TaoNguoiDungRequest {
  hoDem: string
  ten: string
  email: string
  matKhau?: string
  soDienThoai?: string
  cccd?: string
  ngaySinh?: string
  gioiTinh?: 'NAM' | 'NU'
  vaiTro?: VaiTro
  trangThai?: TrangThaiNguoiDung
}

export interface SuaNguoiDungRequest {
  hoDem: string
  ten: string
  soDienThoai: string
}

export interface TaoNguoiDungExcelRequest {
  mssv: string
  hoDem: string
  ten: string
  lop?: string
  khoa?: string
  email: string
}
export interface ChangePasswordRequest {
  matKhauCu: string
  matKhauMoi: string
  xacNhanMatKhau: string
}
