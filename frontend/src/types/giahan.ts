/**
 * giahan.ts — Kiểu dữ liệu cho Gia hạn mượn sách.
 */
import type { NguoiDung } from './nguoidung'

export type TrangThaiGiaHan = 'CHO_DUYET' | 'DA_DUYET' | 'TU_CHOI'

export interface LichSuGiaHan {
  maGiaHan: number
  nguoiDung: Pick<NguoiDung, 'maNguoiDung' | 'hoDem' | 'ten'>
  tenSach: string
  maBarcodeVatLy: string
  hanTraHienTai: string
  hanTraXinGiaHan: string
  trangThai: TrangThaiGiaHan
  lyDoTuChoi?: string
}

export interface DuyetGiaHanRequest {
  hanTraMoi: string
}

export interface TuChoiGiaHanRequest {
  lyDo: string
}
