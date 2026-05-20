/**
 * phat.ts — Kiểu dữ liệu cho Phiếu phạt.
 */
import type { NguoiDung } from './nguoidung'

export type TrangThaiPhieuPhat = 'CHUA_THANH_TOAN' | 'DA_THANH_TOAN'
export type LyDoPhat = 'TRA_TRE' | 'HU_HONG' | 'MAT_SACH'
export type PhuongThucThanhToan = 'TIEN_MAT' | 'CHUYEN_KHOAN'

export interface PhieuPhat {
  maPhieuPhat: number
  nguoiDung: Pick<NguoiDung, 'maNguoiDung' | 'hoDem' | 'ten' | 'email'>
  maPhieuMuon: number
  lyDo: LyDoPhat
  soTienPhat: number
  ngayTao: string
  trangThai: TrangThaiPhieuPhat
  phuongThucThanhToan?: PhuongThucThanhToan
  // Thông tin cuốn sách bị phạt
  tenSach?: string
  maVachCuonSach?: string
}

export interface TaoPhieuPhatRequest {
  phieuMuonId: number
  lyDo: LyDoPhat
  soTienPhat: number
}

export interface ThanhToanRequest {
  phuongThucThanhToan: PhuongThucThanhToan
}
