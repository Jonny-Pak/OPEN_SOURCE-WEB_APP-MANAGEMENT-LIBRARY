/**
 * muonsach.ts — Kiểu dữ liệu cho Phiếu mượn sách và Chi tiết phiếu mượn.
 */
import type { NguoiDung } from './nguoidung'
import type { TinhTrangVatLy } from './sach'

export type TrangThaiPhieuMuon = 'CHUA_HOAN_TAT' | 'DA_HOAN_TAT' | 'DA_HUY'
export type TrangThaiChiTietPhieuMuon = 'DANG_MUON' | 'DA_TRA' | 'DA_TRA_TRE' | 'QUA_HAN' | 'MAT_SACH' | 'DA_HUY'

export interface ChiTietPhieuMuon {
  maChiTietPhieuMuon: string
  maCuonSach: number
  maVach: string
  tenSach: string
  anhBiaUrl?: string
  hanTraBanDau: string
  hanTraHienTai: string
  ngayTraThucTe?: string
  tinhTrangLucMuon: TinhTrangVatLy
  tinhTrangLucTra?: TinhTrangVatLy
  trangThaiChiTietPhieuMuon: TrangThaiChiTietPhieuMuon
  soLanGiaHan: number
  donGiaPhatApDung: number
}

export interface PhieuMuon {
  maPhieuMuon: string
  maNguoiDung: string
  tenDocGia: string
  ngayMuon: string
  trangThaiPhieu: TrangThaiPhieuMuon
  danhSachChiTiet: ChiTietPhieuMuon[]
}

export interface TaoPhieuMuonRequest {
  nguoiDungId: string
  hanTra: string
}

export interface ThemChiTietRequest {
  maBarcodeList: string[]
}

export interface TraSachChiTietItem {
  chiTietId: number
  tinhTrangTraSach: TinhTrangVatLy
}

export interface TraSachRequest {
  chiTietList: TraSachChiTietItem[]
}
