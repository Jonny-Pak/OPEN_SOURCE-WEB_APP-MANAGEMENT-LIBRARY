/**
 * sach.ts — Kiểu dữ liệu cho Đầu sách và Cuốn sách (bản sao vật lý).
 */
import type { TacGia, NhaXuatBan, TheLoai } from './danhmuc'

export interface HinhAnhSach {
  maHinhAnh: number;
  duongDan: string;
  loaiHinhAnh: string;
  thuTuHienThi: number;
  maSach?: number;
}

// ===== ĐẦU SÁCH =====
export interface Sach {
  maSach: number
  tenSach: string
  maIsbn: string
  namXuatBan: number
  moTa?: string
  lanTaiBan?: number
  soTrang?: number
  giaTien?: number
  donGiaPhatTheoNgay?: number
  kichThuoc?: string
  dichGia?: string
  nhaXuatBan: NhaXuatBan
  danhSachTacGia: TacGia[]
  danhSachTheLoai: TheLoai[]
  danhSachHinhAnh?: HinhAnhSach[]
  soLuongCoSan?: number
  soLuongKho?: number
  tongSoLuong?: number
}

export interface TaoSachRequest {
  tenSach: string
  maIsbn: string
  namXuatBan: number
  lanTaiBan: number
  soTrang: number
  ngonNgu?: string
  moTa?: string
  donGiaPhatTheoNgay?: number
  giaTien: number
  kichThuoc?: string
  dichGia?: string
  maNhaXuatBan: number
  maTacGias: number[]
  maTheLoais: number[]
}

// ===== CUỐN SÁCH (bản sao vật lý) =====
export type TinhTrangVatLy = 'TOT' | 'HU_HONG' | 'MAT'
export type TrangThaiCuonSach = 'SAN_SANG' | 'DANG_MUON' | 'CHO_MUON' | 'BAO_MAT'

export interface CuonSach {
  maCuonSach: number
  maBarcodeVatLy: string
  viTriKe: string
  tinhTrangVatLy: TinhTrangVatLy
  trangThai: TrangThaiCuonSach
  sach: Pick<Sach, 'maSach' | 'tenSach' | 'maIsbn'>
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
