/**
 * sach.ts — Kiểu dữ liệu cho Đầu sách và Cuốn sách (bản sao vật lý).
 */
import type { TacGia, NhaXuatBan, TheLoai } from './danhmuc'

// ===== ĐẦU SÁCH =====
export interface Sach {
  maSach: number
  tenSach: string
  isbn: string
  namXuatBan: number
  moTa?: string
  anhBiaUrl?: string
  nhaXuatBan: NhaXuatBan
  tacGias: TacGia[]
  theLoais: TheLoai[]
}

export interface TaoSachRequest {
  tenSach: string
  isbn: string
  namXuatBan: number
  moTa?: string
  nhaXuatBanId: number
  tacGiaIds: number[]
  theLoaiIds: number[]
}

// ===== CUỐN SÁCH (bản sao vật lý) =====
export type TinhTrangVatLy = 'TOT' | 'HU_HONG' | 'MAT'
export type TrangThaiCuonSach = 'TRONG' | 'DA_MUON' | 'BAO_TRI'

export interface CuonSach {
  maCuonSach: number
  maBarcodeVatLy: string
  viTriKe: string
  tinhTrangVatLy: TinhTrangVatLy
  trangThai: TrangThaiCuonSach
  sach: Pick<Sach, 'maSach' | 'tenSach' | 'isbn'>
}

export interface TaoCuonSachRequest {
  sachId: number
  viTriKe: string
  tinhTrangVatLy: TinhTrangVatLy
}

export interface SuaCuonSachRequest {
  viTriKe: string
  tinhTrangVatLy: TinhTrangVatLy
}
