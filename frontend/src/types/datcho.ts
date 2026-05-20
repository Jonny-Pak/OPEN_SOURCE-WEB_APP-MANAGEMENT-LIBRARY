/**
 * datcho.ts — Kiểu dữ liệu cho Đặt chỗ mượn sách.
 */
import type { NguoiDung } from './nguoidung'
import type { Sach } from './sach'

export type TrangThaiDatCho = 'CHO_DUYET' | 'DA_SAN_SANG' | 'DA_HUY' | 'DA_MUON'

export interface DatCho {
  maDatCho: number
  nguoiDung: Pick<NguoiDung, 'maNguoiDung' | 'hoDem' | 'ten' | 'email'>
  sach: Pick<Sach, 'maSach' | 'tenSach' | 'maIsbn'>
  ngayDatCho: string
  ngayHetHan: string
  trangThai: TrangThaiDatCho
}

export interface HuyDatChoRequest {
  lyDo: string
}
