/**
 * muonsach.ts — Kiểu dữ liệu cho Phiếu mượn sách và Chi tiết phiếu mượn.
 */
import type { NguoiDung } from './nguoidung'
import type { TinhTrangVatLy } from './sach'

export type TrangThaiPhieuMuon = 'DANG_MUON' | 'DA_TRA' | 'QUA_HAN'

export interface ChiTietPhieuMuon {
  maChiTiet: number
  maBarcodeVatLy: string
  tenSach: string
  hanTra: string
  ngayTra?: string
  tinhTrangTraSach?: TinhTrangVatLy
  quaHan: boolean
}

export interface PhieuMuon {
  maPhieuMuon: number
  nguoiDung: Pick<NguoiDung, 'maNguoiDung' | 'hoDem' | 'ten' | 'email'>
  ngayMuon: string
  hanTra: string
  trangThai: TrangThaiPhieuMuon
  soLuongCuon: number
  chiTietList: ChiTietPhieuMuon[]
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
